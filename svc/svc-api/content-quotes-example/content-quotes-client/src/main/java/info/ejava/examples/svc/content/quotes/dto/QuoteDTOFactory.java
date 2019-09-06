package info.ejava.examples.svc.content.quotes.dto;

import com.github.javafaker.Faker;

import java.time.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class QuoteDTOFactory {
    private static AtomicInteger nextId = new AtomicInteger(1);
    private final Faker faker = new Faker();

    public int id() { return faker.number().numberBetween(1, 1000); }
    public String author() { return faker.hitchhikersGuideToTheGalaxy().character(); }
    public String text() { return faker.hitchhikersGuideToTheGalaxy().quote(); }
    public LocalDate date() {
        return toDate(faker.date().past(100*365, TimeUnit.DAYS).toInstant());
    }

    public QuoteDTO make() {
        return QuoteDTO.builder()
                .author(author())
                .text(text())
                .date(date())
                .build();
    }
    public QuoteDTO make(UnaryOperator<QuoteDTO>...visitors) {
        final QuoteDTO result = make();
        Stream.of(visitors).forEach(v->v.apply(result));
        return result;
    }

    public static UnaryOperator<QuoteDTO> oneUpId = o-> {
        o.setId(nextId.getAndAdd(1));
        return o;
    };

    public static LocalDate toDate(Instant timestamp) {
            //remove time information
        return timestamp.atOffset(ZoneOffset.UTC)
                .toLocalDate();
    }

    public QuoteListDTOFactory listBuilder() {
        return new QuoteListDTOFactory();
    }

    public class QuoteListDTOFactory {
        public String keywords(int min, int max) {
            return IntStream.range(0, faker.number().numberBetween(min, max))
                    .mapToObj(i->faker.company().buzzword())
                    .collect(Collectors.joining(" "));
        }
        public List<QuoteDTO> quotes(int min, int max, UnaryOperator<QuoteDTO>...visitors) {
            return IntStream.range(0, faker.number().numberBetween(min, max))
                    .mapToObj(i->QuoteDTOFactory.this.make(visitors))
                    .collect(Collectors.toList());
        }

        public QuoteListDTO make(int min, int max, UnaryOperator<QuoteDTO>...visitors) {
            return QuoteListDTO.builder()
                    .quotes(quotes(min, max, visitors))
                    .build();
        }
    }
}
