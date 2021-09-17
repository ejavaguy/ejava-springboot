package info.ejava.assignments.api.race.client.factories;

import com.github.javafaker.Faker;
import info.ejava.assignments.api.race.client.StreetAddressDTO;
import info.ejava.assignments.api.race.client.races.RaceDTO;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RaceDTOFactory {
    private static final AtomicInteger ID = new AtomicInteger();
    private static final AtomicInteger COUNT = new AtomicInteger();
    private static final double[] EVENT_LENGTH = { 5.0, 10.0 };
    private static final String[] EVENT_SUFFIX = { "Run", "Cup", "Series", "Classic", "Challenge"};
    private final Faker faker = new Faker();
    private final StreetAddressDTOFactory streetAddressFactory = new StreetAddressDTOFactory();
    public static String id() { return Integer.valueOf(ID.incrementAndGet()).toString(); }
    public String name() { return name(streetAddressFactory.make()); }
    public String name(StreetAddressDTO address) { return address.getCity() + " " + EVENT_SUFFIX[COUNT.get()%EVENT_SUFFIX.length]; }
    public LocalDate eventDate() { return eventDateFuture(); }
    public LocalDate eventDateFuture() {
        return LocalDate.ofInstant(faker.date()
                        .future(365, TimeUnit.DAYS)
                        .toInstant()
                        .plus(7, ChronoUnit.DAYS), ZoneOffset.UTC);
    }
    public LocalDate eventDatePast() {
        return LocalDate.ofInstant(faker.date()
                        .past(10, TimeUnit.DAYS)
                        .toInstant()
                        .minus(7, ChronoUnit.DAYS), ZoneOffset.UTC);
    }
    public Double eventLength() { return EVENT_LENGTH[COUNT.get() % EVENT_LENGTH.length]; }
    public StreetAddressDTO location() { return streetAddressFactory.make(); }

    public RaceDTO make(UnaryOperator<RaceDTO>...visitors) {
        final StreetAddressDTO location = location();
        final RaceDTO result = RaceDTO.builder()
                .id(null)
                .name(name(location))
                .eventDate(eventDate())
                .location(location)
                .eventLength(eventLength())
                .build();
        Stream.of(visitors).forEach(v->v.apply(result));
        COUNT.incrementAndGet();
        return result;
    }

    public static UnaryOperator<RaceDTO> withId = race -> {
        race.setId(id());
        return race;
    };

    public RaceListDTOFactory listBuilder() { return new RaceListDTOFactory(); }

    public class RaceListDTOFactory {
        public List<RaceDTO> make(int min, int max, UnaryOperator<RaceDTO>...visitors) {
            return IntStream.range(0, faker.number().numberBetween(min, max))
                    .mapToObj(i-> RaceDTOFactory.this.make(visitors))
                    .collect(Collectors.toList());
        }
    }

}
