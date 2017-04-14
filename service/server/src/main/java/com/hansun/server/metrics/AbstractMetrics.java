package com.hansun.server.metrics;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

public abstract class AbstractMetrics {
    protected final StatsdClient statsdClient;
    protected final String prefix;

    private static final Joiner joiner = Joiner.on('.');

    protected AbstractMetrics(StatsdClient statsdClient, String prefix) {
        this.statsdClient = Preconditions.checkNotNull(statsdClient, "statsdClient");
        this.prefix = Preconditions.checkNotNull(prefix, "prefix");
    }

    protected String stat(Object part, Object... parts) {
        return joiner.join(prefix, part, parts);
    }
}
