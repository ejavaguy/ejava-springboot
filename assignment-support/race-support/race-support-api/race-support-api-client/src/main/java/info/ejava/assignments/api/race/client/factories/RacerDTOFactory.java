package info.ejava.assignments.api.race.client.factories;

import com.github.javafaker.Faker;
import info.ejava.assignments.api.race.client.racers.RacerDTO;

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

public class RacerDTOFactory {
    private static final AtomicInteger ID = new AtomicInteger();
    private final Faker faker = new Faker();
    private final StreetAddressDTOFactory streetAddressFactory = new StreetAddressDTOFactory();
    public static String id() { return Integer.valueOf(ID.incrementAndGet()).toString(); }
    public String gender() { return RacerDTO.GENDERS[ID.get() % RacerDTO.GENDERS.length]; }
    public String firstName() { return faker.name().firstName(); }
    public String lastName() { return faker.name().lastName(); }
    public LocalDate dob() {
        return LocalDate.ofInstant(faker.date()
                        .past(365*80, TimeUnit.DAYS)
                        .toInstant()
                        .minus(365*15, ChronoUnit.DAYS), ZoneOffset.UTC);
    }

    public RacerDTO make(UnaryOperator<RacerDTO>...visitors) {
        final RacerDTO result = RacerDTO.builder()
                .id(null)
                .dob(dob())
                .firstName(firstName())
                .lastName(lastName())
                .gender(gender())
                .build();
        Stream.of(visitors).forEach(v->v.apply(result));
        return result;
    }

    public static UnaryOperator<RacerDTO> withId = race -> {
        race.setId(id());
        return race;
    };

    public RacerDTOFactory.RacerListDTOFactory listBuilder() { return new RacerDTOFactory.RacerListDTOFactory(); }

    public class RacerListDTOFactory {
        public List<RacerDTO> make(int min, int max, UnaryOperator<RacerDTO>...visitors) {
            return IntStream.range(0, faker.number().numberBetween(min, max))
                    .mapToObj(i-> RacerDTOFactory.this.make(visitors))
                    .collect(Collectors.toList());
        }
    }

}
