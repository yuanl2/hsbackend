package com.hansun.server.metrics;

public class HSServiceMetrics extends InfluxDBMetrics {
    protected HSServiceMetrics(Builder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends InfluxDBMetrics.Builder<Builder> {

        public Builder measurement(String measurement) {
            return super.measurement(measurement);
        }

        public Builder duration(long duration) {
            return field(Metrics.FIELD_DURATION, duration);
        }

        public Builder gauge(int gauge) {
            return field(Metrics.FIELD_GAUGE, gauge);
        }

        public Builder message(String message) {
            return field(Metrics.FIELD_MESSAGE, message);
        }

        public HSServiceMetrics build() {
            return new HSServiceMetrics(this);
        }
    }
}
