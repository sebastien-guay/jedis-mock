package com.github.fppt.jedismock.server;

import com.github.fppt.jedismock.commands.RedisCommand;
import com.github.fppt.jedismock.operations.OperationFactory;
import com.github.fppt.jedismock.operations.RO_exec;
import com.github.fppt.jedismock.operations.RO_multi;
import com.github.fppt.jedismock.operations.RO_quit;
import com.github.fppt.jedismock.operations.RO_subscribe;
import com.github.fppt.jedismock.operations.RO_unsubscribe;
import com.github.fppt.jedismock.operations.RedisOperation;
import com.github.fppt.jedismock.storage.RedisBase;
import com.github.fppt.jedismock.exception.WrongValueTypeException;
import com.google.common.base.Preconditions;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Xiaolu on 2015/4/20.
 */
public class RedisOperationExecutor {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(RedisOperationExecutor.class);
    private final RedisClient owner;
    private final Map<Integer, RedisBase> redisBases;
    private boolean transactionModeOn;
    private List<RedisOperation> transaction;
    private int selectedRedisBase = 0;

    public RedisOperationExecutor(Map<Integer, RedisBase> redisBases, RedisClient owner) {
        this.redisBases = redisBases;
        this.owner = owner;
        transactionModeOn = false;
        transaction = new ArrayList<>();
    }

    private RedisBase getCurrentBase(){
        return redisBases.computeIfAbsent(selectedRedisBase, key -> new RedisBase());
    }

    private Optional<Slice> buildMetaRedisOperation(String name, List<Slice> params){
        if(name.equals("multi")){
            Slice result = new RO_multi(this::newTransaction).execute();
            return Optional.of(Response.clientResponse(name, result));
        }

        if(name.equals("select")){
            changeActiveRedisBase(params);
            return Optional.of(Response.clientResponse(name, Response.OK));
        }

        switch(name){
            case "subscribe":
                return Optional.of(new RO_subscribe(getCurrentBase(), owner, params).execute());
            case "unsubscribe":
                return Optional.of(new RO_unsubscribe(getCurrentBase(), owner, params).execute());
            case "quit":
                return Optional.of(new RO_quit(owner).execute());
            case "exec":
                transactionModeOn = false;
                return Optional.of(new RO_exec(transaction).execute());
            default:
                return Optional.empty();
        }
    }

    public synchronized Slice execCommand(RedisCommand command) {
        Preconditions.checkArgument(command.parameters().size() > 0);
        List<Slice> params = command.parameters();
        List<Slice> commandParams = params.subList(1, params.size());
        String name = new String(params.get(0).data()).toLowerCase();

        try {
            Optional<Slice> result = buildMetaRedisOperation(name, commandParams);
            if(result.isPresent()) return result.get();

            //Checking if we mutating the transaction or the redisBases
            RedisOperation redisOperation = OperationFactory.buildSimpleOperation(getCurrentBase(), name, commandParams);
            if(transactionModeOn){
                transaction.add(redisOperation);
            } else {
                return Response.clientResponse(name, redisOperation.execute());
            }

            return Response.clientResponse(name, Response.OK);
        } catch(UnsupportedOperationException | WrongValueTypeException | IllegalArgumentException e){
            LOG.error("Malformed request", e);
            return Response.error(e.getMessage());
        }
    }

    private void changeActiveRedisBase(List<Slice> commandParams) {
        String data = new String(commandParams.get(0).data());
        selectedRedisBase = Integer.parseInt(data);
    }

    private synchronized void newTransaction(){
        if(transactionModeOn) throw new RuntimeException("Redis mock does not support more than one transaction");
        transactionModeOn = true;
    }
}
