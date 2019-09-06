package info.ejava.examples.svc.content.quotes.dto;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static info.ejava.examples.svc.content.quotes.dto.QuoteDTOFactory.oneUpId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.BDDAssertions.then;

@Slf4j
public abstract class MarshallingTestBase {
    protected static final Faker faker = new Faker();
    public static ZonedDateTime randomZdt() {
        return ZonedDateTime.ofInstant(faker.date().past(100*365, TimeUnit.DAYS).toInstant(),ZoneOffset.UTC);
    }
    public static final TimeZone UTC_TZ = TimeZone.getTimeZone("UTC");

    public static final String ISO_8601_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX"; //1976-07-04T00:00:00.123Z, .123+00
    public static final String RFC_822_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"; //1976-07-04T00:00:00.123+0000
    public static final String ISO_8601_DATETIME4_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXX"; //1976-07-04T00:00:00.123+0000
    public static final String ISO_8601_DATETIME5_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"; //1976-07-04T00:00:00.123+00:00
//    public static final String ISO_8601_DATETIME5_FORMAT_DTF = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSxxx"; //1976-07-04T00:00:00.123+00:00

    public final String DATES_XML=
    //    "<q:dates xmlns:q=\"urn:ejava.svc-controllers.quotes\"><date>%s</date></q:dates>";
    "<q:dates xmlns:q=\"urn:ejava.svc-controllers.quotes\"><date>%s</date></q:dates>";

    public final String DATES_JSON=
            "{\"date\" : \"%s\"}";


    protected static ZonedDateTime jul4Utc = ZonedDateTime.of(1776, 7, 4, 0, 0, 0, 123456789, ZoneId.of("UTC"));
    protected QuoteDTOFactory quoteDTOFactory = new QuoteDTOFactory();

    public void init() {}
    
    protected abstract <T> String marshal(T object) throws Exception;
    protected abstract <T> T unmarshal(Class<T> type, String buffer) throws Exception;
    protected abstract String get_marshalled_adate(String dateText);
    protected abstract String get_date(String marshalledDates);

    private <T> T marshal_and_demarshal(T obj, Class<T> type) throws Exception {
        String buffer = marshal(obj);
        T result = unmarshal(type, buffer);
        return result;
    }

    @Test
    public void quote_dto_marshals() throws Exception {
        //given - a quote
        QuoteDTO quote = quoteDTOFactory.make();
        quote.setIgnored("ignored");

        //when - marshalled to a string and demarshalled back to an object
        QuoteDTO result = marshal_and_demarshal(quote, QuoteDTO.class);

        //then
        then(result.getText()).isEqualTo(quote.getText());
        then(result.getAuthor()).isEqualTo(quote.getAuthor());
        then(result.getDate()).isEqualTo(quote.getDate());
        then(result.getIgnored()).isNull();

        log.info("date={}", quote.getDate());
        DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE;
        log.info("dtf= {}",dtf.format(quote.getDate()));
    }

    @Test
    public void quotesList_dto_marshals() throws Exception {
        //given - some quotes
        QuoteListDTO quotesList = quoteDTOFactory.listBuilder().make(3,3, oneUpId);

        //when
        QuoteListDTO result = marshal_and_demarshal(quotesList, QuoteListDTO.class);

        //then
        then(result.getCount()).isEqualTo(quotesList.getCount());
        Map<Integer, QuoteDTO> quoteMap = result.getQuotes().stream().collect(Collectors.toMap(QuoteDTO::getId, q->q));
        for (QuoteDTO expected: quotesList.getQuotes()) {
            QuoteDTO actual = quoteMap.remove(expected.getId());
            then(actual).isNotNull();
            then(actual.getText()).isEqualTo(expected.getText());
            then(actual.getAuthor()).isEqualTo(expected.getAuthor());
            then(actual.getDate()).isEqualTo(expected.getDate());
        }
    }

    @Test
    public void message_dto_marshals() throws Exception {
        //given
        MessageDTO msg = MessageDTO.builder()
                .url("http://foo.com")
                .text("testing")
                .build();

        //when
        MessageDTO result = marshal_and_demarshal(msg, MessageDTO.class);

        //then
        then(result.getText()).isEqualTo(msg.getText());
    }

    @ParameterizedTest
    @MethodSource("read_from_formats")
    public void parse_date(String dateText, String name, Date date) throws Exception {
        //given - a known date with a specific format added to the marshalled body
        String body = get_marshalled_adate(dateText);
        log.info("{} => {}", name, dateText);

        //when unmarshalled
        ADate dates=null;
        try {
            dates = unmarshal(ADate.class, body);
        } catch (Exception ex) {
            log.debug("{}", ex.toString());
            fail(ex.toString());
        }
        assertThat(dates.getDate()).isEqualTo(date);
    }

    public static Stream<Arguments> read_from_formats() {
        List<Arguments> params = new ArrayList<>();
        for (Object[] spec: new Object[][]{
                new Object[]{ "ISO_OFFSET_DATE_TIME", DateTimeFormatter.ISO_OFFSET_DATE_TIME, ZoneOffset.UTC },
                new Object[]{ "yyyy-MM-dd'T'HH:mm:ss.SSSX", null },
                new Object[]{ "yyyy-MM-dd'T'HH:mm:ss.SSSXX", null },
                new Object[]{ "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", null },
                new Object[]{ "yyyy-MM-dd'T'HH:mm:ss.SSSXXX[z]", null },
                new Object[]{ "yyyy-MM-dd'T'HH:mm:ss.SSSXXX[z]", null },
        }) {
            for (ZoneId zid : new ZoneId[]{ZoneOffset.UTC, ZoneOffset.ofHours(-5)}) {
                for (int nanos : new int[]{123456789, 100000000, 0}) {
                    ZonedDateTime zdt = jul4Utc.withNano(nanos).withZoneSameInstant(zid);
                    Date date = Date.from(zdt.toInstant());

                    String dateText = null;
                    if (spec[1]==null) {
                        SimpleDateFormat sdf = new SimpleDateFormat((String) spec[0]);
                        sdf.setTimeZone(TimeZone.getTimeZone(zid));
                        dateText = sdf.format(date);
                    } else {
                        OffsetDateTime odt = OffsetDateTime.ofInstant(date.toInstant(), zid);
                        log.info("{}", spec[1]);
                        dateText = ((DateTimeFormatter) spec[1]).format(odt);
                    }
                    params.add(Arguments.of(dateText, spec[0], date));
                }
            }
        }
        return params.stream();
    }

    protected boolean canParseFormat(String format, ZoneOffset tzo) {
        return true;
    }

    public static Stream<Arguments> read_by_formats() {
        List<Arguments> params = new ArrayList<>();
        for (ZoneId zid: new ZoneId[]{ ZoneOffset.UTC, ZoneOffset.ofHours(-5)}) {
            for (int nanos: new int[]{123456789, 100000000, 0}) {
                ZonedDateTime zdt = jul4Utc.withNano(nanos).withZoneSameInstant(zid);
                params.add(Arguments.of(zdt, "ISODateFormat.UNMARSHALLER", ISODateFormat.UNMARSHALER));
                params.add(Arguments.of(zdt, "ISO_OFFSET_DATE_TIME", DateTimeFormatter.ISO_OFFSET_DATE_TIME));
//                params.add(Arguments.of(zdt, null, "yyyy-MM-dd'T'HH:mm:ss[.SSS]X"));
//                params.add(Arguments.of(zdt, null, "yyyy-MM-dd'T'HH:mm:ss[.SSS]XX"));
//                params.add(Arguments.of(zdt, null, "yyyy-MM-dd'T'HH:mm:ss[.SSS]XXX"));
            }
        }
        return params.stream();
    }


    @ParameterizedTest
    @MethodSource("read_by_formats")
    public void marshal_dates(ZonedDateTime zdt, String name, Object format) throws Exception {
        //marshall an object with a date using the baseline parser
        ADate dates = ADate.of(zdt);
        String text = marshal(dates);
        name = (format instanceof DateTimeFormatter) ? name : (String)format;

        //extract the date out of the text payload
        String dateText = get_date(text);
        log.info("{} {}", dateText, dates);
        //log.info("{} {} parsed {}", format, (tz==null? "null":tz.getID()), dateText);
        log.info("{} parsed {}", name, dateText);

        //parse it with a variable DTF format
        DateTimeFormatter dtf = null;
        if (format instanceof DateTimeFormatter) {
            dtf = (DateTimeFormatter) format;
        } else {
            dtf = DateTimeFormatter.ofPattern((String)format);
        }
        Date date = Date.from(ZonedDateTime.parse(dateText, dtf).toInstant());
        assertThat(date).isEqualTo(Date.from(zdt.toInstant()));
    }

}
