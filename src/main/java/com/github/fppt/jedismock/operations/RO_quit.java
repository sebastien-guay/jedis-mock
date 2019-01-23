package com.github.fppt.jedismock.operations;

import com.github.fppt.jedismock.server.RedisClient;
import com.github.fppt.jedismock.server.Response;
import com.github.fppt.jedismock.server.Slice;

public class RO_quit implements RedisOperation {
    private final RedisClient client;

    public RO_quit(RedisClient client) {
        this.client = client;
    }

    @Override
    public Slice execute() {
        client.sendResponse(Response.clientResponse("quit", Response.OK), "quit");
        client.close();
        return Response.SKIP;
    }
}
