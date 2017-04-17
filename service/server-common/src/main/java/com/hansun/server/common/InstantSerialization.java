package com.hansun.server.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class InstantSerialization {

    /**
     * The ISO date-time formatter that formats or parses a date-time with an offset, such as '2011-12-03T10:15:30+01:00'.
     */
    public static final DateTimeFormatter ISO_OFFSET_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public static String asString(Instant date) {
        // Time should have no millis and set to UTC time zone
        return DateTimeFormatter.ISO_INSTANT.format(date.truncatedTo(ChronoUnit.SECONDS));
    }

    public static Instant fromString(String date) {
        // Note: Instant returned is adjusted to UTC.
        return ISO_OFFSET_DATE_TIME_FORMATTER.parse(date, Instant::from);
    }

    public static class ISOInstantDeserializerFasterXML extends JsonDeserializer<Instant> {

        @Override
        public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            if (jsonParser.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
                return Instant.now().truncatedTo(ChronoUnit.MILLIS).plusSeconds(jsonParser.getIntValue());
            } else {
                return ISO_OFFSET_DATE_TIME_FORMATTER.parse(jsonParser.getText(), Instant::from);
            }
        }
    }

    private abstract static class InstantSerializerFasterXML extends JsonSerializer<Instant> {
        protected final DateTimeFormatter formatter;

        InstantSerializerFasterXML(DateTimeFormatter dateTimeFormatter) {
            this.formatter = dateTimeFormatter;
        }

        @Override
        public void serialize(
                Instant date,
                JsonGenerator jsonGenerator,
                SerializerProvider serializerProvider) throws IOException {
            if (date != null) {
                jsonGenerator.writeString(formatter.format(date));
            }
        }
    }

    public static class ISOInstantNoMillisSerializerFasterXML extends InstantSerializerFasterXML {

        public ISOInstantNoMillisSerializerFasterXML() {
            super(DateTimeFormatter.ISO_INSTANT);
        }

        @Override
        public void serialize(
                Instant date,
                JsonGenerator jsonGenerator,
                SerializerProvider serializerProvider) throws IOException {
            if (date != null) {
                jsonGenerator.writeString(formatter.format(date.truncatedTo(ChronoUnit.SECONDS)));
            }
        }
    }

    public static class ISOInstantSerializerFasterXML extends InstantSerializerFasterXML {

        public ISOInstantSerializerFasterXML() {
            super(DateTimeFormatter.ISO_INSTANT);
        }
    }
}
