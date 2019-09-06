package info.ejava.examples.svc.content.quotes.dto;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public abstract class JaxbTimeAdapters<T> extends XmlAdapter<String, T> {
    @Override
    public T unmarshal(String text) throws Exception {
        return text == null ? null : doParse(text, ISODateFormat.UNMARSHALER);
    }

    @Override
    public String marshal(T timestamp) throws Exception {
        return timestamp==null ? null : doFormat(ISODateFormat.MARSHALER, timestamp);
    }

    protected abstract T doParse(String text, DateTimeFormatter dtf);
    protected abstract String doFormat(DateTimeFormatter dtf, T timestamp);

    //a simpler "first example"
    public static class LocalDateJaxbAdapter extends JaxbTimeAdapters<LocalDate> {
        @Override
        public LocalDate unmarshal(String text) {
            return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
        }
        @Override
        public String marshal(LocalDate timestamp) {
            return DateTimeFormatter.ISO_LOCAL_DATE.format(timestamp);
        }

        @Override
        protected LocalDate doParse(String text, DateTimeFormatter dtf) { return unmarshal(text); }
        @Override
        protected String doFormat(DateTimeFormatter dtf, LocalDate timestamp) { return marshal(timestamp); }
    }

    public static class InstantJaxbAdapter extends JaxbTimeAdapters<Instant> {
        @Override
        protected Instant doParse(String text, DateTimeFormatter dtf) {
            return ZonedDateTime.parse(text, dtf).toInstant();
        }
        @Override
        protected String doFormat(DateTimeFormatter dtf, Instant timestamp) {
            return timestamp.atOffset(ZoneOffset.UTC).toInstant().toString();
        }
    }

    public static class LocalDateTimeJaxbAdapter extends JaxbTimeAdapters<LocalDateTime> {
        public static final DateTimeFormatter LDT_MARSHALER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        @Override
        protected LocalDateTime doParse(String text, DateTimeFormatter dtf) {
            return LocalDateTime.parse(text, dtf);
        }
        @Override
        protected String doFormat(DateTimeFormatter dtf, LocalDateTime timestamp) {
            return LDT_MARSHALER.format(timestamp);

        }
    }

    /*
    public static class LocalDateJaxbAdapter extends JaxbTimeAdapters<LocalDate> {
        public static final DateTimeFormatter LD_MARSHALER = DateTimeFormatter.ISO_LOCAL_DATE;
        @Override
        protected LocalDate doParse(String text, DateTimeFormatter dtf) {
            return LocalDate.parse(text, LD_MARSHALER);
        }
        @Override
        protected String doFormat(DateTimeFormatter dtf, LocalDate timestamp) {
            return LD_MARSHALER.format(timestamp);

        }
    }
     */

    public static class ZonedDateTimeJaxbAdapter extends JaxbTimeAdapters<ZonedDateTime> {
        @Override
        protected ZonedDateTime doParse(String text, DateTimeFormatter dtf) {
            return ZonedDateTime.parse(text, dtf);
        }
        @Override
        protected String doFormat(DateTimeFormatter dtf, ZonedDateTime timestamp) {
            return dtf.format(timestamp);
        }
    }

    public static class OffsetDateTimeJaxbAdapter extends JaxbTimeAdapters<OffsetDateTime> {
        @Override
        protected OffsetDateTime doParse(String text, DateTimeFormatter dtf) {
            return OffsetDateTime.parse(text, dtf);
        }
        @Override
        protected String doFormat(DateTimeFormatter dtf, OffsetDateTime timestamp) {
            return dtf.format(timestamp);
        }
    }

    public static class DateJaxbAdapter extends JaxbTimeAdapters<Date> {
        @Override
        protected Date doParse(String text, DateTimeFormatter dtf) {
            return Date.from(ZonedDateTime.parse(text, dtf).toInstant());
        }

        @Override
        protected String doFormat(DateTimeFormatter dtf, Date timestamp) {
            ZonedDateTime zdt = ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC);
            return dtf.format(zdt);
        }
    }
}
