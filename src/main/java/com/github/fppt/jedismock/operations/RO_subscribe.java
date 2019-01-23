package com.github.fppt.jedismock.operations;

import com.github.fppt.jedismock.server.RedisClient;
import com.github.fppt.jedismock.server.Response;
import com.github.fppt.jedismock.server.Slice;
import com.github.fppt.jedismock.storage.RedisBase;

import java.util.List;

public class RO_subscribe extends AbstractRedisOperation {
    private final RedisClient client;

    public RO_subscribe(RedisBase base, RedisClient client, List<Slice> params) {
        super(base, params);
        this.client = client;
    }

    Slice response() {
        params().forEach(channel -> base().addSubscriber(channel, client));
        List<Slice> numSubscriptions = base().getSubscriptions(client);

        return Response.subscribedToChannel(numSubscriptions);
    }
}
