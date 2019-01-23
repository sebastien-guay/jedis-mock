package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.RedisClient;
import com.github.fppt.jedismock.RedisCommand;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;
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

    private RedisOperation buildSimpleOperation(String name, List<Slice> params){
        RedisOperations foundOperation;
        try {
            foundOperation = RedisOperations.valueOf(name);
        } catch(IllegalArgumentException e){
            throw new UnsupportedOperationException(String.format("Unsupported operation '%s'", name));
        }
        return foundOperation.factory().apply(getCurrentBase(), params);
    }

    private Optional<Slice> buildMetaRedisOperation(String name, List<Slice> params){
        if(name.equals(MetaRedisOperations.MULTI.name())){
            newTransaction();
            return Optional.of(Response.clientResponse(name, Response.OK));
        }

        if(name.equals(MetaRedisOperations.SELECT.name())){
            changeActiveRedisBase(params);
            return Optional.of(Response.clientResponse(name, Response.OK));
        }

        try {
            MetaRedisOperations operation = MetaRedisOperations.valueOf(name);
            switch(operation){
                case SUBSCRIBE:
                    return Optional.of(new RO_subscribe(getCurrentBase(), owner, params).execute());
                case UNSUBSCRIBE:
                    return Optional.of(new RO_unsubscribe(getCurrentBase(), owner, params).execute());
                case QUIT:
                    return Optional.of(new RO_quit(owner).execute());
                case EXEC:
                    transactionModeOn = false;
                    return Optional.of(new RO_exec(transaction).execute());
            }
        } catch (IllegalArgumentException ignored){

        }

        return Optional.empty();
    }

    public synchronized Slice execCommand(RedisCommand command) {
        Preconditions.checkArgument(command.parameters().size() > 0);
        List<Slice> params = command.parameters();
        List<Slice> commandParams = params.subList(1, params.size());
        String name = new String(params.get(0).data()).toUpperCase();

        try {
            Optional<Slice> result = buildMetaRedisOperation(name, commandParams);
            if(result.isPresent()) return result.get();

            //Checking if we mutating the transaction or the redisBases
            RedisOperation redisOperation = buildSimpleOperation(name, commandParams);
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

    private void newTransaction(){
        if(transactionModeOn) throw new RuntimeException("Redis mock does not support more than one transaction");
        transactionModeOn = true;
    }
}
