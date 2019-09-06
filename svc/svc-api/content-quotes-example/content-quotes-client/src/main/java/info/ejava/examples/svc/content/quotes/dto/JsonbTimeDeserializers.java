package info.ejava.examples.svc.content.quotes.dto;

import org.eclipse.yasson.internal.JsonbParser;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public abstract class JsonbTimeDeserializers<T> implements JsonbDeserializer<T> {
    protected abstract T doParse(String text, DateTimeFormatter dtf);

    @Override
    public T deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        JsonParser.Event event = ((JsonbParser) parser).getCurrentLevel().getLastEvent();
        if (event == JsonParser.Event.VALUE_NULL) {
            return null;
        } else {
            String text = parser.getString();
            return text == null ? null : doParse(text, ISODateFormat.UNMARSHALER);
        }
    }

    public static class InstantJsonbDeserializer extends JsonbTimeDeserializers<Instant> {
       @Override
        protected Instant doParse(String text, DateTimeFormatter dtf) {
            return ZonedDateTime.parse(text, dtf).toInstant();
        }
    }
    public static class LocalDateTimeJsonbDeserializer extends JsonbTimeDeserializers<LocalDateTime> {
        @Override
        protected LocalDateTime doParse(String text, DateTimeFormatter dtf) {
            return LocalDateTime.parse(text, dtf);
        }
    }
    public static class ZonedDateTimeJsonbDeserializer extends JsonbTimeDeserializers<ZonedDateTime> {
        @Override
        protected ZonedDateTime doParse(String text, DateTimeFormatter dtf) {
            return ZonedDateTime.parse(text, dtf);
        }
    }
    public static class OffsetDateTimeJsonbDeserializer extends JsonbTimeDeserializers<OffsetDateTime> {
        @Override
        protected OffsetDateTime doParse(String text, DateTimeFormatter dtf) {
            return OffsetDateTime.parse(text, dtf);
        }
    }
    public static class DateJsonbDeserializer extends JsonbTimeDeserializers<Date> {
        @Override
        protected Date doParse(String text, DateTimeFormatter dtf) {
            return Date.from(ZonedDateTime.parse(text, dtf).toInstant());
        }
    }
}

