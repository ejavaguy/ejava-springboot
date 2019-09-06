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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static info.ejava.examples.db.mongo.books.dto.BookDTOFactory.nextDate;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes={NTestConfiguration.class})
@Tag("springboot")
@ActiveProfiles(resolver = TestProfileResolver.class)
@Slf4j
@DisplayName("Repository Custom Methods")
public class BooksCustomRepositoryNTest {
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
        IntStream.range(0,5).forEach(i->{
            Book book = mapper.map(dtoFactory.make(nextDate));
            savedBooks.add(book);
        });
        booksRepository.saveAll(savedBooks);
    }

    @Test
    void custom_extension(){
        //when
        Optional<Book> randomBook = booksRepository.random();

        //then
        then(randomBook.isPresent()).isTrue();
        then(randomBook.get()).isNotNull();
    }


    @Test
    void findBy_as_data_query() {
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
        List<String> foundTitles = booksRepository.findByTitleGESizeAsString(minLength);
        log.info("title size GE '{}' found {}", minLength, foundTitles);

        //then
        then(new HashSet<>(foundTitles)).isEqualTo(titles);
    }
}
