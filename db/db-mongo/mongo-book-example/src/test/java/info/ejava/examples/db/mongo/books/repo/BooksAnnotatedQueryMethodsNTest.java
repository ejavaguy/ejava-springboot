package info.ejava.examples.db.mongo.books.repo;

import info.ejava.examples.db.mongo.books.TestProfileResolver;
import info.ejava.examples.db.mongo.books.bo.Book;
import info.ejava.examples.db.mongo.books.NTestConfiguration;
import info.ejava.examples.db.mongo.books.dto.BookDTOFactory;
import info.ejava.examples.db.mongo.books.svc.BooksMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static info.ejava.examples.db.mongo.books.dto.BookDTOFactory.nextDate;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes={NTestConfiguration.class})
@Tag("springboot")
@ActiveProfiles(resolver = TestProfileResolver.class)
@Slf4j
@DisplayName("Repository Annotated @Query Methods")
public class BooksAnnotatedQueryMethodsNTest {
    @Autowired
    private BooksRepository booksRepository;
    @Autowired
    private BookDTOFactory dtoFactory;
    @Autowired
    private BooksMapper mapper;

    private List<Book> savedBooks = new ArrayList<>();

    @BeforeEach
    void populate() {
        booksRepository.deleteAll();
        IntStream.range(0,3).forEach(i->{
            Book book = mapper.map(dtoFactory.make(nextDate));
            if (i==2) {
                book.setTitle(null);
            }
            booksRepository.save(book);
            savedBooks.add(book);
            //log.info("{}", book);
        });
    }

    @Test
    void findByBetween() {
        //given
        Book firstBook = savedBooks.get(0);
        Book lastBook = savedBooks.get(savedBooks.size()-1);
        Set<String> expectedIds = savedBooks.stream()
                .filter(s->!(s.getPublished().isBefore(firstBook.getPublished()) || s.getPublished().isAfter(lastBook.getPublished())))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Book> foundBooks = booksRepository.findByPublishedBetween(firstBook.getPublished(), lastBook.getPublished());
        log.info("released between '{}' and '{}' found {}", firstBook.getPublished(), lastBook.getPublished(), foundBooks);

        //then
        Set<String> foundIds = foundBooks.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(expectedIds);
    }


    @Test
    void return_specific_fields_as_object_query() {
        //given
        int minLength = savedBooks.stream()
                .mapToInt(s-> (s.getTitle()==null ? 0 : s.getTitle().length()) )
                .max()
                .getAsInt();
        Set<String> titles = savedBooks.stream()
                .filter(s-> s.getTitle()!=null && s.getTitle().length() >= minLength)
                .map(s->s.getTitle())
                .collect(Collectors.toSet());

        //when
        List<String> foundTitles = booksRepository.getTitlesGESizeAsBook(minLength).stream()
                .map(s->s.getTitle())
                .collect(Collectors.toList());
        log.info("title size GE '{}' found {}", minLength, foundTitles);

        //then
        then(new HashSet<>(foundTitles)).isEqualTo(titles);
    }

    @Test
    void findBy_size_of_author() {
        //given
        int minLength = savedBooks.stream()
                .mapToInt(s-> (s.getAuthor()==null ? 0 : s.getAuthor().length()) )
                .max()
                .getAsInt()-1;
        Set<String> ids = savedBooks.stream()
                .filter(s->s.getAuthor()!=null && s.getAuthor().length() >= minLength)
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when - using a typed db.find()
        List<Book> foundBooks = booksRepository.findByAuthorGESize(minLength);
        log.info("title size GE '{}' found {}", minLength, foundBooks);

        //then
        Set<String> foundIds = foundBooks.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(ids);
    }
}
