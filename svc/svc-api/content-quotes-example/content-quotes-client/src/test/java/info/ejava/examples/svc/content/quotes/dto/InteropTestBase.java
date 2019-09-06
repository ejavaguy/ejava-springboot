package info.ejava.examples.svc.content.quotes.dto;

import org.junit.jupiter.params.provider.Arguments;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static info.ejava.examples.svc.content.quotes.dto.QuoteDTOFactory.oneUpId;
import static org.assertj.core.api.Assertions.assertThat;

public class InteropTestBase {
    protected static ZonedDateTime jul4Utc = ZonedDateTime.of(1776, 7, 4, 8, 2, 4, 123456789, ZoneOffset.UTC);

    protected static MessageDTO msg = new MessageDTO("http://testing", "testing");
    protected static QuoteDTO quote = new QuoteDTOFactory().make(oneUpId);
    protected static QuoteListDTO quotes = new QuoteDTOFactory().listBuilder().make(3, 3, oneUpId);

    protected static ADate dates = ADate.of(jul4Utc);
    protected static ADate datesNomsecs = ADate.of(jul4Utc.withNano(0));
    protected static ADate dates5micro = ADate.of(jul4Utc.withNano(123450000));
    protected static ADate datesEST = ADate.of(ZonedDateTime.of(jul4Utc.toLocalDateTime(), ZoneId.of("EST", ZoneId.SHORT_IDS)));
    protected static ADate datesEST5micro = ADate.of(ZonedDateTime.of(jul4Utc.toLocalDateTime(), ZoneId.of("EST", ZoneId.SHORT_IDS)).withNano(123450000));
    protected static ADate dates0430micro = ADate.of(ZonedDateTime.of(jul4Utc.toLocalDateTime(), ZoneOffset.ofHoursMinutes(4, 30)));

    private static Stream<Arguments> dtos() {
        return Stream.of(
                Arguments.of(dates),
                Arguments.of(datesNomsecs),
                Arguments.of(dates5micro),
                Arguments.of(datesEST),
                Arguments.of(datesEST5micro),
                Arguments.of(dates0430micro),
                Arguments.of(msg),
                Arguments.of(quote),
                Arguments.of(quotes)
        );
    }


    protected void compareTimes(ADate request, ADate result) {
        ZonedDateTime zdtUtc = request.getZdt().withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime dateUtc = ZonedDateTime.ofInstant(result.getInstant(), ZoneOffset.UTC);
        assertThat(zdtUtc).isEqualTo(dateUtc);
    }
}
