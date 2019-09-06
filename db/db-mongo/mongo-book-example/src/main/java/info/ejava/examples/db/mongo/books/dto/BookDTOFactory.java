package info.ejava.examples.db.mongo.books.dto;

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

public class BookDTOFactory {
    private static AtomicInteger nextId = new AtomicInteger(1);
    private static AtomicLong nextEpochDay = new AtomicLong(
            LocalDate.of(2000, 1, 1).toEpochDay());
    private final Faker faker = new Faker();

    public String author() { return faker.book().author(); }
    //some tests are relying on title to be unique
    public String title() { return faker.book().title() + " " + faker.color().name(); }
    public LocalDate published() {
        return faker.date()
                .past(20*365, TimeUnit.DAYS)
                .toInstant()
                .atOffset(ZoneOffset.UTC)
                .toLocalDate();
    }

    public BookDTO make(UnaryOperator<BookDTO>...visitors) {
        final BookDTO result = BookDTO.builder()
                .title(title())
                .author(author())
                .published(published())
                .build();
        Stream.of(visitors).forEach(v->v.apply(result));
        return result;
    }

    public static UnaryOperator<BookDTO> oneUpId = s->{
        s.setId(Integer.valueOf(nextId.getAndAdd(1)).toString());
        return s;
    };

    public static UnaryOperator<BookDTO> nextDate = s->{
        LocalDate ld = LocalDate.ofEpochDay(nextEpochDay.getAndAdd(1L));
        s.setPublished(ld);
        return s;
    };

    public BooksListDTOFactory listBuilder() { return new BooksListDTOFactory(); }

    public class BooksListDTOFactory {
        public List<BookDTO> books(int min, int max, UnaryOperator<BookDTO>...visitors) {
            return IntStream.range(0, faker.number().numberBetween(min, max))
                    .mapToObj(i-> BookDTOFactory.this.make(visitors))
                    .collect(Collectors.toList());
        }
    }
}
