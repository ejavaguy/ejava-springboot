package info.ejava.examples.db.jpa.songs.dto;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SongDTOFactory {
    private static AtomicInteger nextId = new AtomicInteger(1);
    private static AtomicLong nextEpochDay = new AtomicLong(
            LocalDate.of(2000, 1, 1).toEpochDay());
    private final Faker faker = new Faker();

    public String artist() { return faker.rockBand().name(); }
    //some tests are relying on title to be unique
    public String title() { return faker.book().title() + " " + faker.color().name(); }
    public LocalDate released() {
        return faker.date()
                .past(20*365, TimeUnit.DAYS)
                .toInstant()
                .atOffset(ZoneOffset.UTC)
                .toLocalDate();
    }

    public SongDTO make(UnaryOperator<SongDTO>...visitors) {
        final SongDTO result = SongDTO.builder()
                .title(title())
                .artist(artist())
                .released(released())
                .build();
        Stream.of(visitors).forEach(v->v.apply(result));
        return result;
    }

    public static UnaryOperator<SongDTO> oneUpId = s->{
        s.setId(nextId.getAndAdd(1));
        return s;
    };

    public static UnaryOperator<SongDTO> nextDate = s->{
        LocalDate ld = LocalDate.ofEpochDay(nextEpochDay.getAndAdd(1L));
        s.setReleased(ld);
        return s;
    };

    public SongsListDTOFactory listBuilder() { return new SongsListDTOFactory(); }

    public class SongsListDTOFactory {
        public List<SongDTO> songs(int min, int max, UnaryOperator<SongDTO>...visitors) {
            return IntStream.range(0, faker.number().numberBetween(min, max))
                    .mapToObj(i->SongDTOFactory.this.make(visitors))
                    .collect(Collectors.toList());
        }
    }
}
