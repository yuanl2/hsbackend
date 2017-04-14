package com.hansun.server.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;

//TODO: finish converting this entire class to use MetricRegistry.  It's unclear who, if anyone, is relying on the
//existing metric names so I'm punting for now.
public class DnsMetrics {
    private Counter dnsSuccess;
    private Counter dnsTimeouts;
    private Counter dnsErrors;
    private  Histogram dnsQueryTime;
    protected  MetricRegistry metricRegistry;

    public DnsMetrics(MetricRegistry metricRegistry) {
        init(metricRegistry, "");

    }

    private DnsMetrics(MetricRegistry metricRegistry,String instance) {
        init(metricRegistry, instance + ".");
    }

    private void init(MetricRegistry metricRegistry,String prefix)
    {
        this.metricRegistry = metricRegistry;
        dnsSuccess = metricRegistry.counter(prefix + "dns.success");
        dnsTimeouts = metricRegistry.counter(prefix + "dns.timeout");
        dnsErrors = metricRegistry.counter(prefix + "dns.error");
        dnsQueryTime = metricRegistry.histogram(prefix + "dns.query_time");

    }

    public void incrementDnsSuccessCount() {
        dnsSuccess.inc();
    }

    public void incrementDnsTimeoutCount() {
        dnsTimeouts.inc();
    }

    public void incrementDnsErrorCount() {
        dnsErrors.inc();
    }

    public void dnsQueryTime(long ms) {
        dnsQueryTime.update(ms);
    }

    public DnsMetrics getInstance(String instance) {
        if(instance == null || instance.isEmpty()) {
            return this;
        }

        return new DnsMetrics(metricRegistry, instance);
    }
}
