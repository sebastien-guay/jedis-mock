package com.github.fppt.jedismock.operations;

import com.github.fppt.jedismock.server.Response;
import com.github.fppt.jedismock.server.Slice;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class RO_exec implements RedisOperation {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(RO_exec.class);
    private final List<RedisOperation> transaction;

    public RO_exec(List<RedisOperation> transaction) {
        this.transaction = transaction;
    }

    @Override
    public Slice execute() {
        try {
            List<Slice> results = transaction.stream().
                    map(RedisOperation::execute).
                    collect(Collectors.toList());
            transaction.clear();
            return Response.array(results);
        } catch (Throwable t){
            LOG.error("ERROR during committing transaction", t);
            return Response.NULL;
        }
    }
}
