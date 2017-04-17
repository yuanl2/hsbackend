package com.hansun.server.metrics;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * Simple statsd client adapted from https://github.com/etsy/statsd/blob/master/examples/StatsdClient.java.
 */
public class StatsdClient implements Closeable {
    private final List<InetSocketAddress> addresses = new ArrayList<>();
    private final Map<InetSocketAddress, DatagramChannel> channels = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private static final Random random = new Random();
    private static final Logger log = LoggerFactory.getLogger(StatsdClient.class);

    /** Please use InfluxDBHelper or InfluxDBClient to emit the metrics data */
    @Deprecated
    public StatsdClient(String host, int port) {
        checkNotNull(host, "host");
        Preconditions.checkArgument(port > 0, "Invalid port");
        InetSocketAddress address = new InetSocketAddress(host, port);
        addresses.add(address);
        openDatagramChannel(address);
    }

    /** Please use InfluxDBHelper or InfluxDBClient to emit the metrics data */
    @Deprecated
	public StatsdClient(List<String> statsdHosts) {
		Preconditions.checkArgument(statsdHosts != null && !statsdHosts.isEmpty(), "statsdHosts");
		for (String statsHost : statsdHosts) {
			String statsdHostParts[] = statsHost.trim().split(":");
			String statsdHostName = statsdHostParts[0];
			int statsdHostPort = Integer.valueOf(statsdHostParts[1]);
			Preconditions.checkArgument(statsdHostName != null && !statsdHostName.trim().isEmpty(), "Invalid statsd hostname: " + statsdHostName);
			Preconditions.checkArgument(statsdHostPort > 0, "Invalid statsd port: " + statsdHostPort);
            InetSocketAddress address = new InetSocketAddress(statsdHostName, statsdHostPort);
			addresses.add(address);
            openDatagramChannel(address);
		}
	}

    public void timing(String stat, long value) {
        timing(stat, value, 1.0);
    }

    public void timing(String stat, long value, double sampleRate) {
        checkArgument(value >= 0, "Invalid timing metric value: " + value);
        send(stat, value, "ms", sampleRate);
    }

    public void increment(String stat) {
        increment(stat, 1.0);
    }

    public void increment(String stat, double sampleRate) {
        update(stat, 1, sampleRate);
    }

    public void decrement(String stat) {
        decrement(stat, 1.0);
    }

    public void decrement(String stat, double sampleRate) {
        update(stat, -1, sampleRate);
    }

    public void update(String stat, long value, double sampleRate) {
        send(stat, value, "c", sampleRate);
    }

    public void gauge(String stat, long value) {
        gauge(stat, value, 1.0);
    }

    public void gauge(String stat, long value, double sampleRate) {
        send(stat, value, "g", sampleRate);
    }

    private void send(String stat, long value, String type, double sampleRate) {
        checkNotNull(stat, "stat");
        checkArgument(sampleRate >= 0.0, "Invalid metric sample rate: " + sampleRate);
        if (sampleRate < 1.0) {
            if (random.nextDouble() <= sampleRate) {
                send(format(Locale.ENGLISH, "%s:%d|%s|@%f", stat, value, type, sampleRate));
            }
        } else {
            send(format(Locale.ENGLISH, "%s:%s|%s", stat, value, type));
        }
    }

    private void send(String stat) {
        ByteBuffer bb = ByteBuffer.wrap(stat.getBytes(Charsets.UTF_8));
        for (InetSocketAddress address : addresses) {
            send(stat, bb, address);
            bb.rewind();
        }
    }

    private void send(String stat, ByteBuffer bb, InetSocketAddress address) {
        try {
            DatagramChannel channel = openDatagramChannel(address);
            channel.send(bb, address);
        } catch (IOException e) {
            log.error(format("Error sending stat '%s' to %s", stat, address), e);
        }
    }

    private DatagramChannel openDatagramChannel(InetSocketAddress address) {
        DatagramChannel channel = channels.get(address);
        if (channel != null && channel.isOpen()) {
            return channel;
        }
        lock.lock();
        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channels.put(address, channel);
        } catch (IOException e) {
            log.error("Error on opening a new DatagramChannel", e);
        } finally {
            lock.unlock();
        }
        return channel;
    }

    @Override
    public void close() {
        lock.lock();
        try {
            for (DatagramChannel channel : channels.values()) {
                channel.close();
            }
        } catch (IOException e) {
            log.error("Error on closing a DatagramChannel", e);
        } finally {
            lock.unlock();
        }
    }
}
