package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Slice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class OperationFactory {
    private static final Map<String, BiFunction<RedisBase, List<Slice>, RedisOperation>> STANDARD_OPERATIONS = new HashMap<>();
    static {
        STANDARD_OPERATIONS.put("set", RO_set::new);
        STANDARD_OPERATIONS.put("setex", RO_setex::new);
        STANDARD_OPERATIONS.put("psetex", RO_psetex::new);
        STANDARD_OPERATIONS.put("setnx", RO_setnx::new);
        STANDARD_OPERATIONS.put("setbit", RO_setbit::new);
        STANDARD_OPERATIONS.put("append", RO_append::new);
        STANDARD_OPERATIONS.put("get", RO_get::new);
        STANDARD_OPERATIONS.put("getbit", RO_getbit::new);
        STANDARD_OPERATIONS.put("ttl", RO_ttl::new);
        STANDARD_OPERATIONS.put("pttl", RO_pttl::new);
        STANDARD_OPERATIONS.put("expire", RO_expire::new);
        STANDARD_OPERATIONS.put("pexpire", RO_pexpire::new);
        STANDARD_OPERATIONS.put("incr", RO_incr::new);
        STANDARD_OPERATIONS.put("incrby", RO_incrby::new);
        STANDARD_OPERATIONS.put("decr", RO_decr::new);
        STANDARD_OPERATIONS.put("decrby", RO_decrby::new);
        STANDARD_OPERATIONS.put("pfcount", RO_pfcount::new);
        STANDARD_OPERATIONS.put("pfadd", RO_pfadd::new);
        STANDARD_OPERATIONS.put("pfmerge", RO_pfmerge::new);
        STANDARD_OPERATIONS.put("mget", RO_mget::new);
        STANDARD_OPERATIONS.put("mset", RO_mset::new);
        STANDARD_OPERATIONS.put("getset", RO_getset::new);
        STANDARD_OPERATIONS.put("strlen", RO_strlen::new);
        STANDARD_OPERATIONS.put("del", RO_del::new);
        STANDARD_OPERATIONS.put("exists", RO_exists::new);
        STANDARD_OPERATIONS.put("expireat", RO_expireat::new);
        STANDARD_OPERATIONS.put("pexpireat", RO_pexpireat::new);
        STANDARD_OPERATIONS.put("lpush", RO_lpush::new);
        STANDARD_OPERATIONS.put("rpush", RO_rpush::new);
        STANDARD_OPERATIONS.put("lpushx", RO_lpushx::new);
        STANDARD_OPERATIONS.put("lrange", RO_lrange::new);
        STANDARD_OPERATIONS.put("llen", RO_llen::new);
        STANDARD_OPERATIONS.put("lpop", RO_lpop::new);
        STANDARD_OPERATIONS.put("rpop", RO_rpop::new);
        STANDARD_OPERATIONS.put("lindex", RO_lindex::new);
        STANDARD_OPERATIONS.put("rpoplpush", RO_rpoplpush::new);
        STANDARD_OPERATIONS.put("brpoplpush", RO_brpoplpush::new);
        STANDARD_OPERATIONS.put("publish", RO_publish::new);
        STANDARD_OPERATIONS.put("flushall", RO_flushall::new);
        STANDARD_OPERATIONS.put("lrem", RO_lrem::new);
        STANDARD_OPERATIONS.put("ping", RO_ping::new);
        STANDARD_OPERATIONS.put("keys", RO_keys::new);
        STANDARD_OPERATIONS.put("sadd", RO_sadd::new);
        STANDARD_OPERATIONS.put("spop", RO_spop::new);
        STANDARD_OPERATIONS.put("hget", RO_hget::new);
        STANDARD_OPERATIONS.put("hset", RO_hset::new);
        STANDARD_OPERATIONS.put("hdel", RO_hdel::new);
        STANDARD_OPERATIONS.put("hgetall", RO_hgetall::new);
        STANDARD_OPERATIONS.put("sinter", RO_sinter::new);
        STANDARD_OPERATIONS.put("hmget", RO_hmget::new);
        STANDARD_OPERATIONS.put("hmset", RO_hmset::new);
        STANDARD_OPERATIONS.put("smembers", RO_smembers::new);
        STANDARD_OPERATIONS.put("hsetnx", RO_hsetnx::new);
    }

    public static RedisOperation buildSimpleOperation(RedisBase base, String name, List<Slice> params){
        BiFunction<RedisBase, List<Slice>, RedisOperation> builder = OperationFactory.STANDARD_OPERATIONS.get(name);
        if(builder == null) throw new UnsupportedOperationException(String.format("Unsupported operation '%s'", name));
        return builder.apply(base, params);
    }


}
