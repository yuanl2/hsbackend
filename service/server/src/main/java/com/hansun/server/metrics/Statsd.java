package com.hansun.server.metrics;

import java.io.IOException;
import java.net.SocketException;

public interface Statsd extends AutoCloseable {
    void connect() throws IllegalStateException, SocketException;
    void send(String name, String value, Statsd.StatType statType) throws IOException;

    public static enum StatType { COUNTER, TIMER, GAUGE }
}
