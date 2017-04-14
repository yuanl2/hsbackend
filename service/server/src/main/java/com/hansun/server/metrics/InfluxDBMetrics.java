package com.hansun.server.metrics;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

public abstract class InfluxDBMetrics {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final String measurement;
    private final Instant timestamp;
    private final Map<String, String> tags;
    private final Map<String, Object> fields;

    protected InfluxDBMetrics(Builder<?> builder) {
        this.measurement = builder.measurement;
        this.tags = builder.tags;
        this.fields = builder.fields;
        this.timestamp = builder.timestamp != null ? builder.timestamp : Instant.now();
    }

    public String getMeasurement() {
        return measurement;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getTagMap() {
        return tags;
    }

    public Map<String, Object> getFieldMap() {
        return fields;
    }

    public String getTagValue(String tagKey) {
        return tags.get(tagKey);
    }

    public Object getFieldValue(String fieldKey) {
        return fields.get(fieldKey);
    }

    public static abstract class Builder<B extends Builder<B>> {
        private String measurement;
        private Instant timestamp;
        private final Map<String, String> tags = Maps.newTreeMap(Ordering.natural());
        private final Map<String, Object> fields = Maps.newTreeMap(Ordering.natural());

        public B measurement(final String measurement) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(measurement), "measurement must not be null or empty");
            this.measurement = measurement;
            return (B) this;
        }

        public B timestamp(final Instant timestamp) {
            Preconditions.checkArgument(timestamp != null, "timestamp must not be null");
            this.timestamp = timestamp;
            return (B) this;
        }

        public B tag(final String tagName, final boolean value) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(tagName), "tag name must not be null or empty");
            tags.put(tagName, String.valueOf(value));
            return (B) this;
        }

        public B tag(final String tagName, final int value) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(tagName), "tag name must not be null or empty");
            tags.put(tagName, String.valueOf(value));
            return (B) this;
        }

        public B tag(final String tagName, final String value) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(tagName), "tag name must not be null or empty");
            if (value != null) {
                tags.put(tagName, value);
            }
            return (B) this;
        }

        public B field(final String fieldName, final Object value) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(fieldName), "field name must not be null or empty");
            if (value != null) {
                if (value instanceof Number || value instanceof Boolean) {
                    fields.put(fieldName, value);
                } else if (value instanceof Date) {
                    fields.put(fieldName, ((Date)value).toInstant().toString());
                } else {
                    fields.put(fieldName, value.toString());
                }
            }
            return (B) this;
        }
    }
}
