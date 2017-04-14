package com.hansun.server.metrics;

public class BatcherMetrics extends AbstractMetrics {
    public BatcherMetrics(StatsdClient statsdClient, String prefix) {
        super(statsdClient, prefix);
    }

    public void callEventQueueFull() {
        statsdClient.increment("diagnostics.batcher.queue.full");
    }

}
