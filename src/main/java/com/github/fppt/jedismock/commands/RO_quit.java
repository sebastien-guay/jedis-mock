package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisClient;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;

class RO_quit implements RedisOperation {
    private final RedisClient client;

    RO_quit(RedisClient client) {
        this.client = client;
    }

    @Override
    public Slice execute() {
        client.sendResponse(Response.clientResponse("quit", Response.OK), "quit");
        client.close();
        return Response.SKIP;
    }
}
