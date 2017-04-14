package com.hansun.server.metrics;

import com.codahale.metrics.Counter;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class FirstDerivativeCounter extends Counter {
    private Supplier<Number> supplier;
    private AtomicLong priorCount = new AtomicLong();

    public FirstDerivativeCounter(Supplier<Number> supplier) {
        this.supplier = supplier;
    }

    public void inc() {
        throw new UnsupportedOperationException();
    }

    public void inc(long n) {
        throw new UnsupportedOperationException();
    }

    public void dec() {
        dec(1);
    }

    public void dec(long n) {
        // We're accumulating the amount that we've decremented. This is subtracted from
        // the running total in getCount() below, to convert to a period-of-time value.
        priorCount.addAndGet(n);
    }

    @Override
    public long getCount() {
        long currentCount = supplier.get().longValue();

        // priorCount will be maintained by StatsdReporter's invocation of dec(). If we change
        // that behavior, we could increment here instead.
        return currentCount - priorCount.get();
    }

    long getPriorCount() {
        return priorCount.get();
    }
}