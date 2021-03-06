package com.hansun.server.metrics;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;


@Service
public class HSServiceMetricsService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private InfluxDBClientHelper influxDbClientHelper;

    @PostConstruct
    private void init() {
        HSServiceMetrics.Builder builder = HSServiceMetrics.builder();
        builder.measurement(Metrics.ORDER_FINISH).device(String.valueOf(20170400001l)).area("test").user("user1")
                .count(1).duration(15).price(5.0f);
        sendMetrics(builder.build());
    }

    public void sendMetrics(HSServiceMetrics metrics) {
        influxDbClientHelper.emitMetrics(metrics);
        logger.info("Metric {} sent", metrics.getMeasurement());
    }

    public void sendMetrics(String metricsName) {
        if (StringUtils.isEmpty(metricsName)) {
            return;
        }

        HSServiceMetrics metrics = HSServiceMetrics.builder()
                .measurement(metricsName)
                .build();
        sendMetrics(metrics);
    }

    public void sendMetrics(String metricsName, String message) {
        if (StringUtils.isEmpty(metricsName)) {
            return;
        }

        HSServiceMetrics metrics = HSServiceMetrics.builder()
                .measurement(metricsName)
                .message(message)
                .build();
        sendMetrics(metrics);
    }

    public void sendDurationMetrics(String metricsName, long duration) {
        if (StringUtils.isEmpty(metricsName)) {
            return;
        }

        HSServiceMetrics metrics = HSServiceMetrics.builder()
                .measurement(metricsName)
                .duration(duration)
                .build();
        sendMetrics(metrics);
    }

    public void sendGaugeMetrics(String metricsName, int gauge) {
        if (StringUtils.isEmpty(metricsName)) {
            return;
        }

        HSServiceMetrics metrics = HSServiceMetrics.builder()
                .measurement(metricsName)
                .gauge(gauge)
                .build();
        sendMetrics(metrics);
    }
}
