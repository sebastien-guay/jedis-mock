package com.github.fppt.jedismock.operations;

import com.github.fppt.jedismock.server.RedisClient;
import com.github.fppt.jedismock.server.Response;
import com.github.fppt.jedismock.server.Slice;
import com.github.fppt.jedismock.storage.RedisBase;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RO_unsubscribe extends AbstractRedisOperation {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(RO_unsubscribe.class);
    private final RedisClient client;

    public RO_unsubscribe(RedisBase base, RedisClient client, List<Slice> params) {
        super(base, params);
        this.client = client;
    }

    Slice response() {
        List<Slice> channelsToUbsubscribeFrom;
        if(params().isEmpty()){
            LOG.debug("No channels specified therefore unsubscribing from all channels");
            channelsToUbsubscribeFrom = base().getSubscriptions(client);
        } else {
            channelsToUbsubscribeFrom = params();
        }

        for (Slice channel : channelsToUbsubscribeFrom) {
            LOG.debug("Unsubscribing from channel [" + channel + "]");
            if(base().removeSubscriber(channel, client)) {
                int numSubscriptions = base().getSubscriptions(client).size();
                Slice response = Response.unsubscribe(channel, numSubscriptions);
                client.sendResponse(Response.clientResponse("unsubscribe", response), "unsubscribe");
            }
        }

        //Skip is sent because we have already responded
        return Response.SKIP;
    }
}
