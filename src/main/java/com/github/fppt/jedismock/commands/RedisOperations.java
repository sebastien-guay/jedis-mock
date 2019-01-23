package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Slice;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Lists all operations which affects the {@link com.github.fppt.jedismock.RedisBase} with no side effects.
 * This can be thought of as simple atomic operations.
 */
public enum RedisOperations {
    SET(RO_set::new),
    SETEX(RO_setex::new),
    PSETEX(RO_psetex::new),
    SETNX(RO_setnx::new),
    SETBIT(RO_setbit::new),
    APPEND(RO_append::new),
    GET(RO_get::new),
    GETBIT(RO_getbit::new),
    TTL(RO_ttl::new),
    PTTL(RO_pttl::new),
    EXPIRE(RO_expire::new),
    PEXPIRE(RO_pexpire::new),
    INCR(RO_incr::new),
    INCRBY(RO_incrby::new),
    DECR(RO_decr::new),
    DECRBY(RO_decrby::new),
    PFCOUNT(RO_pfcount::new),
    PFADD(RO_pfadd::new),
    PFMERGE(RO_pfmerge::new),
    MGET(RO_mget::new),
    MSET(RO_mset::new),
    GETSET(RO_getset::new),
    STRLEN(RO_strlen::new),
    DEL(RO_del::new),
    EXISTS(RO_exists::new),
    EXPIREAT(RO_expireat::new),
    PEXPIREAT(RO_pexpireat::new),
    LPUSH(RO_lpush::new),
    RPUSH(RO_rpush::new),
    LPUSHX(RO_lpushx::new),
    LRANGE(RO_lrange::new),
    LLEN(RO_llen::new),
    LPOP(RO_lpop::new),
    RPOP(RO_rpop::new),
    LINDEX(RO_lindex::new),
    RPOPLPUSH(RO_rpoplpush::new),
    BRPOPLPUSH(RO_brpoplpush::new),
    PUBLISH(RO_publish::new),
    FLUSHALL(RO_flushall::new),
    LREM(RO_lrem::new),
    PING(RO_ping::new),
    KEYS(RO_keys::new),
    SADD(RO_sadd::new),
    SPOP(RO_spop::new),
    HGET(RO_hget::new),
    HSET(RO_hset::new),
    HDEL(RO_hdel::new),
    HGETALL(RO_hgetall::new),
    SINTER(RO_sinter::new),
    HMGET(RO_hmget::new),
    HMSET(RO_hmset::new),
    SMEMBERS(RO_smembers::new);

    private BiFunction<RedisBase, List<Slice>, RedisOperation> factory;
    RedisOperations(BiFunction<RedisBase, List<Slice>, RedisOperation> factory){
        this.factory = factory;
    }

    public BiFunction<RedisBase, List<Slice>, RedisOperation> factory(){
        return factory;
    }

}
