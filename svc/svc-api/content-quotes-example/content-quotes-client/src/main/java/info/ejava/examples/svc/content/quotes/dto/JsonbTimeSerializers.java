package info.ejava.examples.svc.content.quotes.dto;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public abstract class JsonbTimeSerializers<T> implements JsonbSerializer<T> {
    protected abstract String doFormat(DateTimeFormatter dtf, T timestamp);

    @Override
    public void serialize(T timestamp, JsonGenerator generator, SerializationContext ctx) {
        generator.write(doFormat(ISODateFormat.MARSHALER, timestamp));
    }

    /**
     * Need a simple, first serializer here
     */
    public static class DateJsonbSerializer implements JsonbSerializer<Date> {
        @Override
        public void serialize(Date date, JsonGenerator generator, SerializationContext serializationContext) {
            generator.write(DateTimeFormatter.ISO_INSTANT.format(date.toInstant()));
        }
    }

    public static class InstantJsonbSerializer extends JsonbTimeSerializers<Instant> {
       @Override
        protected String doFormat(DateTimeFormatter dtf, Instant timestamp) {
            return dtf.format(timestamp);
        }
    }
    public static class LocalDateTimeJsonbSerializer extends JsonbTimeSerializers<LocalDateTime> {
        @Override
        protected String doFormat(DateTimeFormatter dtf, LocalDateTime timestamp) {
            return dtf.format(timestamp);
        }
    }
    public static class ZonedDateTimeJsonbSerializer extends JsonbTimeSerializers<ZonedDateTime> {
        @Override
        protected String doFormat(DateTimeFormatter dtf, ZonedDateTime timestamp) {
            return dtf.format(timestamp);
        }
    }
    public static class OffsetDateTimeJsonbSerializer extends JsonbTimeSerializers<OffsetDateTime> {
        @Override
        protected String doFormat(DateTimeFormatter dtf, OffsetDateTime timestamp) {
            return dtf.format(timestamp);
        }
    }
/*
    public static class DateJsonbSerializer extends JsonbTimeSerializers<Date> {
        @Override
        protected String doFormat(DateTimeFormatter dtf, Date timestamp) {
            return dtf.format(OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC));
        }
    }
*/
}

