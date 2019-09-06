package info.ejava.examples.svc.content.quotes.dto;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class DateTest {
    private static final ZonedDateTime DATE = ZonedDateTime.of(1776,7,4,5,2,3,123456789, ZoneId.of("UTC"));

    private static final DateTimeFormatter[] FORMATTERS() {
        return new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]X"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSS]X"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSSSSS]X"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.n]X"),
                DateTimeFormatter.ISO_OFFSET_DATE_TIME,
                ISODateFormat.UNMARSHALER
        };
    };

    private static final Stream<Arguments> dateText() {
        return Stream.of(
          Arguments.of("1776-07-04T05:02:03",DATE.withNano(000000000)),
          Arguments.of("1776-07-04T05:02:03Z",DATE.withNano(000000000)),
          Arguments.of("1776-07-04T05:02:03.123Z",DATE.withNano(123000000)),
          Arguments.of("1776-07-04T05:02:03.12345Z",DATE.withNano(123450000)),
          Arguments.of("1776-07-04T05:02:03.123456Z",DATE.withNano(123456000)),
          Arguments.of("1776-07-04T05:02:03.123456789Z",DATE),
          Arguments.of("1776-07-04T05:02:03.123-00",DATE.withNano(123000000)),
          Arguments.of("1776-07-04T05:02:03.123-0000",DATE.withNano(123000000)),
          Arguments.of("1776-07-04T05:02:03.123-00:00",DATE.withNano(123000000)),
          Arguments.of("1776-07-04T00:02:03.123-05", DATE.withNano(123000000)),
          Arguments.of("1776-07-04T00:02:03.123-0500", DATE.withNano(123000000)),
          Arguments.of("1776-07-04T00:02:03.123-05:00", DATE.withNano(123000000)),
          Arguments.of("1776-07-04T00:32:03.123-0430", DATE.withNano(123000000)),
          Arguments.of("1776-07-04T00:32:03.123-04:30", DATE.withNano(123000000)),
          Arguments.of("1776-07-04T05:02:03.123456789Z[UTC]",DATE),
          Arguments.of("1776-07-04T05:02:03.123Z[UTC]",DATE.withNano(123000000))
        );
    };

    static ZonedDateTime toEST(ZonedDateTime utc) {
        return ZonedDateTime.of(utc.toLocalDateTime(), ZoneId.of("EST", ZoneId.SHORT_IDS));
    }

    @ParameterizedTest
    @MethodSource("dateText")
    public void formatters(String dateText, ZonedDateTime dateValue) {
        for (DateTimeFormatter dtf: FORMATTERS()) {
            log.info("{}", dtf.format(dateValue));
        }
    }

    @ParameterizedTest
    @MethodSource("dateText")
    public void parse_custom(String dateText, ZonedDateTime dateValue) throws ParseException {
//        DateTimeFormatter dtf = ISODateFormat.UNMARSHALER;
        DateTimeFormatter dtf = new DateTimeFormatterBuilder()
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

        ZonedDateTime zdt = ZonedDateTime.parse(dateText, dtf);
        log.info("{} parsed into zdt={}", dateText, zdt);
        OffsetDateTime odt = OffsetDateTime.parse(dateText, dtf);
        log.info("{} parsed into odt={}", dateText, odt);
        LocalDateTime ldt = LocalDateTime.parse(dateText, dtf);
        log.info("{} parsed into ldt={}", dateText, ldt);
        assertThat(zdt).isEqualTo(dateValue);
        assertThat(odt).isEqualTo(dateValue.toOffsetDateTime());
    }

    @ParameterizedTest
    @MethodSource("dateText")
    public void parse_ISO_OFFSET_DATE_TIME(String dateText, ZonedDateTime dateValue) throws ParseException {
        Assumptions.assumeTrue(dateText.matches(".*(Z|-\\d{2}|-\\d{2}:\\d{2})$"),
                "offset not supported by ISO_OFFSET_DATE_TIME: " + dateText);

        DateTimeFormatter dtf = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        ZonedDateTime zdt = ZonedDateTime.parse(dateText, dtf);
        log.info("{} parsed into zdt={}", dateText, zdt);
        LocalDateTime ldt = LocalDateTime.parse(dateText, dtf);
        log.info("{} parsed into ldt={}", dateText, ldt);
    }

    @ParameterizedTest
    @MethodSource("dateText")
    public void parse_nX(String dateText, ZonedDateTime dateValue) throws ParseException {
        Assumptions.assumeTrue(dateText.matches(".*(Z|-\\d{2,4}|-\\d{2}:\\d{2})$"),
                "offset not supported by ISO_OFFSET_DATE_TIME: " + dateText);

        String pattern = "yyyy-MM-dd'T'HH:mm:ss[.n][XXX][X]";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        ZonedDateTime zdt = ZonedDateTime.parse(dateText, dtf);
        log.info("{} parsed into zdt={}", dateText, zdt);
        LocalDateTime ldt = LocalDateTime.parse(dateText, dtf);
        log.info("{} parsed into ldt={}", dateText, ldt);
    }


    @Test
    public void dateRange() {
        Calendar minDate = new Calendar.Builder().setInstant(new Date(Long.MIN_VALUE)).build();
        Calendar maxDate = new Calendar.Builder().setInstant(new Date(Long.MAX_VALUE)).build();

        log.info("Date range - min={} {}, max={} {}",
                minDate.get(Calendar.YEAR), (minDate.get(Calendar.ERA)==0 ? "BC" : "AD"),
                maxDate.get(Calendar.YEAR), (maxDate.get(Calendar.ERA)==0 ? "BC" : "AD"));
    }

    @Test
    public void offsetDateTime_equality() {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime est = utc.withOffsetSameInstant(ZoneOffset.ofHours(-5));
        log.info("utc={}, est={}", utc, est);

        //UTC is logically equivalent to EST
        assertThat(est.toInstant()).isEqualTo(utc.toInstant());
        if (utc.getHour() < est.getHour()) { //UTC advanced to tomorrow
            assertThat(est.getHour()).isEqualTo(utc.getHour()+19);
        } else {
            assertThat(est.getHour()).isEqualTo(utc.getHour()-5);
        }
        assertThat(est.getOffset()).isEqualTo(ZoneOffset.ofHours(-5));
        assertThat(est.isEqual(utc)).isTrue();
        assertThat(utc.isEqual(est)).isTrue();
        assertThat(est.toInstant()).isEqualTo(utc.toInstant());
        assertThat(est).isEqualTo(utc);
        assertThat(est).isAtSameInstantAs(utc);

        //UTC is not technically equal to EST
        assertThat(est.equals(utc)).isFalse();
    }

    @Test
    public void zonedDateTime_equality() {
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime est = utc.withZoneSameInstant(ZoneOffset.ofHours(-5));
        log.info("utc={}, est={}", utc, est);

        //UTC is logically equivalent to EST
        assertThat(est.toInstant()).isEqualTo(utc.toInstant());
        if (utc.getHour() < est.getHour()) { //UTC advanced to tomorrow
            assertThat(est.getHour()).isEqualTo(utc.getHour()+19);
        } else {
            assertThat(est.getHour()).isEqualTo(utc.getHour()-5);
        }
        assertThat(est.getOffset()).isEqualTo(ZoneOffset.ofHours(-5));
        assertThat(est.isEqual(utc)).isTrue();
        assertThat(utc.isEqual(est)).isTrue();
        assertThat(est.toInstant()).isEqualTo(utc.toInstant());

        assertThat(est).isEqualTo(utc);

        //UTC is not technically equal to EST
        assertThat(est.equals(utc)).isFalse();

    }

    @Test
    public void zoneOffset() {
        ZonedDateTime est1976 = ZonedDateTime.of(1976,7,4,10,0,0,0, ZoneId.of("America/New_York"));
        ZonedDateTime est1776 = ZonedDateTime.of(1776,7,4,10,0,0,0, ZoneId.of("America/New_York"));
        log.info("{}", ZoneId.systemDefault());
        log.info("{}", est1776);    //1776-07-04T10:00-04:56:02[America/New_York]
        log.info("{}", est1976);    //1976-07-04T10:00-04:00[America/New_York]
        log.info("{}", est1776.getOffset()); //-04:56:02
        log.info("{}", est1976.getOffset()); //-04:00
        assertThat(est1776.getOffset()).isNotEqualTo(est1976.getOffset());
        assertThat(est1776.getOffset().getTotalSeconds())
                .isEqualTo(est1976.getOffset().getTotalSeconds()-(56*60)-2);
    }
}
