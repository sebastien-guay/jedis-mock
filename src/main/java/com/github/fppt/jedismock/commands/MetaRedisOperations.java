package com.github.fppt.jedismock.commands;

/**
 * Lists operations which impact the {@link com.github.fppt.jedismock.RedisClient} or multiple
 * {@link com.github.fppt.jedismock.RedisBase}s. This can be thought or more complex operations
 */
public enum MetaRedisOperations {
    SUBSCRIBE,
    UNSUBSCRIBE,
    QUIT,
}
