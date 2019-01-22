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

/**
 * Created by Xiaolu on 2015/4/20.
 */
public class RedisOperationExecutor {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(RedisOperationExecutor.class);
    private final RedisClient owner;
    private final List<RedisBase> redisBases;
    private boolean transactionModeOn;
    private List<RedisOperation> transaction;
    private int selectedRedisBase = 0;

    public RedisOperationExecutor(List<RedisBase> redisBases, RedisClient owner) {
        this.redisBases = redisBases;
        this.owner = owner;
        transactionModeOn = false;
        transaction = new ArrayList<>();
    }

    private RedisOperation buildSimpleOperation(String name, List<Slice> params){
        switch(name){
            case "set":
                return new RO_set(redisBases.get(selectedRedisBase), params);
            case "setex":
                return new RO_setex(redisBases.get(selectedRedisBase), params);
            case "psetex":
                return new RO_psetex(redisBases.get(selectedRedisBase), params);
            case "setnx":
                return new RO_setnx(redisBases.get(selectedRedisBase), params);
            case "setbit":
                return new RO_setbit(redisBases.get(selectedRedisBase), params);
            case "append":
                return new RO_append(redisBases.get(selectedRedisBase), params);
            case "get":
                return new RO_get(redisBases.get(selectedRedisBase), params);
            case "getbit":
                return new RO_getbit(redisBases.get(selectedRedisBase), params);
            case "ttl":
                return new RO_ttl(redisBases.get(selectedRedisBase), params);
            case "pttl":
                return new RO_pttl(redisBases.get(selectedRedisBase), params);
            case "expire":
                return new RO_expire(redisBases.get(selectedRedisBase), params);
            case "pexpire":
                return new RO_pexpire(redisBases.get(selectedRedisBase), params);
            case "incr":
                return new RO_incr(redisBases.get(selectedRedisBase), params);
            case "incrby":
                return new RO_incrby(redisBases.get(selectedRedisBase), params);
            case "decr":
                return new RO_decr(redisBases.get(selectedRedisBase), params);
            case "decrby":
                return new RO_decrby(redisBases.get(selectedRedisBase), params);
            case "pfcount":
                return new RO_pfcount(redisBases.get(selectedRedisBase), params);
            case "pfadd":
                return new RO_pfadd(redisBases.get(selectedRedisBase), params);
            case "pfmerge":
                return new RO_pfmerge(redisBases.get(selectedRedisBase), params);
            case "mget":
                return new RO_mget(redisBases.get(selectedRedisBase), params);
            case "mset":
                return new RO_mset(redisBases.get(selectedRedisBase), params);
            case "getset":
                return new RO_getset(redisBases.get(selectedRedisBase), params);
            case "strlen":
                return new RO_strlen(redisBases.get(selectedRedisBase), params);
            case "del":
                return new RO_del(redisBases.get(selectedRedisBase), params);
            case "exists":
                return new RO_exists(redisBases.get(selectedRedisBase), params);
            case "expireat":
                return new RO_expireat(redisBases.get(selectedRedisBase), params);
            case "pexpireat":
                return new RO_pexpireat(redisBases.get(selectedRedisBase), params);
            case "lpush":
                return new RO_lpush(redisBases.get(selectedRedisBase), params);
            case "rpush":
                return new RO_rpush(redisBases.get(selectedRedisBase), params);
            case "lpushx":
                return new RO_lpushx(redisBases.get(selectedRedisBase), params);
            case "lrange":
                return new RO_lrange(redisBases.get(selectedRedisBase), params);
            case "llen":
                return new RO_llen(redisBases.get(selectedRedisBase), params);
            case "lpop":
                return new RO_lpop(redisBases.get(selectedRedisBase), params);
            case "rpop":
                return new RO_rpop(redisBases.get(selectedRedisBase), params);
            case "lindex":
                return new RO_lindex(redisBases.get(selectedRedisBase), params);
            case "rpoplpush":
                return new RO_rpoplpush(redisBases.get(selectedRedisBase), params);
            case "brpoplpush":
                return new RO_brpoplpush(redisBases.get(selectedRedisBase), params);
            case "subscribe":
                return new RO_subscribe(redisBases.get(selectedRedisBase), owner, params);
            case "unsubscribe":
                return new RO_unsubscribe(redisBases.get(selectedRedisBase), owner, params);
            case "publish":
                return new RO_publish(redisBases.get(selectedRedisBase), params);
            case "flushall":
                return new RO_flushall(redisBases.get(selectedRedisBase), params);
            case "lrem":
                return new RO_lrem(redisBases.get(selectedRedisBase), params);
            case "quit":
                return new RO_quit(redisBases.get(selectedRedisBase), owner, params);
            case "exec":
                transactionModeOn = false;
                return new RO_exec(redisBases.get(selectedRedisBase), transaction, params);
            case "ping":
                return new RO_ping(redisBases.get(selectedRedisBase), params);
            case "keys":
                return new RO_keys(redisBases.get(selectedRedisBase), params);
            case "sadd":
                return new RO_sadd(redisBases.get(selectedRedisBase), params);
            case "smembers":
                return new RO_smembers(redisBases.get(selectedRedisBase), params);
            case "spop":
                return new RO_spop(redisBases.get(selectedRedisBase), params);
            case "hget":
                return new RO_hget(redisBases.get(selectedRedisBase), params);
            case "hset":
                return new RO_hset(redisBases.get(selectedRedisBase), params);
            case "hdel":
                return new RO_hdel(redisBases.get(selectedRedisBase), params);
            case "hgetall":
                return new RO_hegetall(redisBases.get(selectedRedisBase), params);
            case "sinter":
                return new RO_sinter(redisBases.get(selectedRedisBase), params);
            case "hmget":
                return new RO_hmget(redisBases.get(selectedRedisBase), params);
            case "hmset":
                return new RO_hmset(redisBases.get(selectedRedisBase), params);
            default:
                throw new UnsupportedOperationException(String.format("Unsupported operation '%s'", name));
        }
    }

    public synchronized Slice execCommand(RedisCommand command) {
        Preconditions.checkArgument(command.parameters().size() > 0);
        List<Slice> params = command.parameters();
        List<Slice> commandParams = params.subList(1, params.size());
        String name = new String(params.get(0).data()).toLowerCase();

        try {
            //Transaction handling
            if(name.equals("multi")){
                newTransaction();
                return Response.clientResponse(name, Response.OK);
            }

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

    private void newTransaction(){
        if(transactionModeOn) throw new RuntimeException("Redis mock does not support more than one transaction");
        transactionModeOn = true;
    }
}
