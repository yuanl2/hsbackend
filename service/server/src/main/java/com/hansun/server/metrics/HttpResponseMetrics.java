package com.hansun.server.metrics;

import java.util.UUID;

public class HttpResponseMetrics extends InfluxDBMetrics {
    /**
     * influxDB measurement
     */
    public static final String HTTP_RESPONSE = "http.response";

    /**
     * influxDB tag names
     */
    public static final String RESPONSE_CODE = "response.code";
    public static final String METHOD = "method";
    public static final String PATH = "path";
    public static final String TEST_CALL = "testcall";

    /**
     * influxDB field names
     */
    public static final String COUNT = "count";
    public static final String DURATION = "duration";
    public static final String LOCUS_ID = "locusId";


    public HttpResponseMetrics(Builder builder) {
        super(builder);
    }

    public String getResponseCode() {
        return getTagValue(RESPONSE_CODE);
    }

    public String getMethod() {
        return getTagValue(METHOD);
    }

    public String getPath() {
        return getTagValue(PATH);
    }

    public boolean isTestCall() {
        return Boolean.parseBoolean(getTagValue(TEST_CALL));
    }

    public int getCount() {
        return (Integer) getFieldValue(COUNT);
    }

    public long getDuration() {
        return (Long) getFieldValue(DURATION);
    }

    public UUID getLocusId() {
        return getFieldValue(LOCUS_ID) != null ? UUID.fromString((String) getFieldValue(LOCUS_ID)) : null;
    }

    public static Builder builder() {
        return new Builder().measurement(HTTP_RESPONSE);
    }

    public static final class Builder extends InfluxDBMetrics.Builder<Builder> {

        public Builder status(int status) {
            return tag(RESPONSE_CODE, status);
        }

        public Builder method(String method) {
            return tag(METHOD, method);
        }

        public Builder path(String path) {
            return tag(PATH, path);
        }

        public Builder testcall(boolean testcall) {
            return tag(TEST_CALL, testcall);
        }

        public Builder count(int count) {
            return field(COUNT, count);
        }

        public Builder duration(long duration) {
            return field(DURATION, duration);
        }

        public Builder locusId(UUID locusId) {
            return field(LOCUS_ID, locusId);
        }

        public HttpResponseMetrics build() {
            return new HttpResponseMetrics(this);
        }
    }
}
