package info.ejava.examples.svc.springfox.contests.dto;

import com.github.javafaker.Faker;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ContestDTOFactory {
    private static AtomicInteger nextId = new AtomicInteger(1);
    private final Faker faker = new Faker();
    private static final int DURATIONS[] = new int[] {30, 60, 90, 120};

    public int score() { return faker.number().numberBetween(0, 100); }
    public String team() { return faker.esports().team(); }
    public boolean completed() { return faker.bool().bool(); }
    public Duration duration() {
        int length = DURATIONS[faker.number().numberBetween(0, DURATIONS.length-1)];
        return Duration.of(length, ChronoUnit.MINUTES);
    }
    public OffsetDateTime scheduledStart() {
        Date past = faker.date().past(365, TimeUnit.DAYS);
        Date future = faker.date().future(365, TimeUnit.DAYS);
        Date date = faker.date().between(past, future);
        ZoneId zoneId = ZoneId.of(faker.address().timeZone());
        return OffsetDateTime.ofInstant(date.toInstant(), zoneId);
    }

    public ContestDTO make() {
        boolean completed = completed();
        return ContestDTO.builder()
                .scheduledStart(scheduledStart())
                .duration(duration())
                .awayTeam(team())
                .homeTeam(team())
                .completed(completed)
                .awayScore(completed ? score() : null)
                .homeScore(completed ? score() : null)
                .build();
    }
    public ContestDTO make(UnaryOperator<ContestDTO>...visitors) {
        final ContestDTO result = make();
        Stream.of(visitors).forEach(v->v.apply(result));
        return result;
    }

    public static UnaryOperator<ContestDTO> oneUpId = o-> {
        o.setId(nextId.getAndAdd(1));
        return o;
    };

    public ContestListDTOFactory listBuilder() {
        return new ContestListDTOFactory();
    }

    public class ContestListDTOFactory {
        public String keywords(int min, int max) {
            return IntStream.range(0, faker.number().numberBetween(min, max))
                    .mapToObj(i->faker.company().buzzword())
                    .collect(Collectors.joining(" "));
        }
        public List<ContestDTO> quotes(int min, int max, UnaryOperator<ContestDTO>...visitors) {
            return IntStream.range(0, faker.number().numberBetween(min, max))
                    .mapToObj(i->ContestDTOFactory.this.make(visitors))
                    .collect(Collectors.toList());
        }

        public ContestListDTO make(int min, int max, UnaryOperator<ContestDTO>...visitors) {
            return ContestListDTO.builder()
                    .contests(quotes(min, max, visitors))
                    .build();
        }
    }
}
