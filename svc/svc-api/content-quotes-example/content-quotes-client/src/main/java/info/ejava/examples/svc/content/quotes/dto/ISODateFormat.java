package info.ejava.examples.svc.content.quotes.dto;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;

public class ISODateFormat extends DateFormat implements Cloneable {
    public static final DateTimeFormatter UNMARSHALER = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .append(DateTimeFormatter.ISO_LOCAL_DATE)
                    .appendLiteral('T')
                    .append(DateTimeFormatter.ISO_LOCAL_TIME)
                    .parseLenient()
                    .optionalStart().appendOffset("+HH", "Z").optionalEnd()
                    .optionalStart().appendOffset("+HH:mm", "Z").optionalEnd()
                    .optionalStart().appendOffset("+HHmm", "Z").optionalEnd()
                    .optionalStart().appendLiteral('[').parseCaseSensitive().appendZoneRegionId().appendLiteral(']').optionalEnd()
                    .parseDefaulting(ChronoField.OFFSET_SECONDS,0)
                    .parseStrict()
                    .toFormatter();
    public static final DateTimeFormatter MARSHALER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    public static final String MARSHAL_ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss[.SSS]XXX";

//    @Override
//    public Date parse(String source) throws ParseException {
//        return parse(source, (ParsePosition)null);
//    }

    @Override
    public Date parse(String source, ParsePosition pos) {
        OffsetDateTime odt = OffsetDateTime.parse(source, UNMARSHALER);
        pos.setIndex(source.length()-1);
        return Date.from(odt.toInstant());
    }

    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
        MARSHALER.formatTo(zdt, toAppendTo);
        return toAppendTo;
    }

    @Override
    public Object clone() {
        return new ISODateFormat(); //we have no state to clone
    }
}
