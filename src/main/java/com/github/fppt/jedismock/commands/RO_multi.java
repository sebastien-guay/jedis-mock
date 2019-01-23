package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;

public class RO_multi implements RedisOperation {
    private Runnable transactionBuilder;

    public RO_multi(Runnable transactionBuilder){
        this.transactionBuilder = transactionBuilder;
    }

    @Override
    public Slice execute() {
        transactionBuilder.run();
        return Response.OK;
    }
}
