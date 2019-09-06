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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static info.ejava.examples.db.mongo.books.dto.BookDTOFactory.nextDate;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes= NTestConfiguration.class)
@Tag("springboot")
@ActiveProfiles(resolver = TestProfileResolver.class)
//@ActiveProfiles(profiles="mongodb", resolver = TestProfileResolver.class)
@DisplayName("Repository Query By Example")
@Slf4j
public class BooksQueryByExampleNTest {
    @Autowired
    private BooksRepository booksRepository;
    @Autowired
    private BookDTOFactory dtoFactory;
    @Autowired
    private BooksMapper mapper;

    private static List<Book> savedBooks = new ArrayList<>();

    @BeforeEach
    void populate() {
        if (savedBooks.isEmpty()) {
            booksRepository.deleteAll();
            IntStream.range(0, 10).forEach(i -> {
                Book book = mapper.map(dtoFactory.make(nextDate));
                savedBooks.add(book);
            });
            booksRepository.saveAll(savedBooks);
        }
    }

    @Test
    void default_matcher_non_null_and() {
        //given
        Book savedBook = savedBooks.get(0);
        Book probe = Book.builder()
                .title(savedBook.getTitle())
                .author(savedBook.getAuthor())
                .build();

        //when
        List<Book> foundBooks = booksRepository.findAll(
                Example.of(probe),//default matcher is matchingAll() and non-null
                Sort.by("id"));

        //then - not found, default matcher included the primary key
        then(foundBooks).hasSize(1);
        then(foundBooks.get(0)).isEqualTo(savedBook);
    }

    /**
     * This test shows how we can combine predicates using an OR.
     */
    @Test
    void matching_any() {
        //given
        Book savedBook = savedBooks.get(0);
        Book probe = Book.builder()
                .title(savedBook.getTitle())
                .author(savedBook.getAuthor())
                .build();

        //when
        List<Book> foundBooks = booksRepository.findAll(
                Example.of(probe, ExampleMatcher.matchingAny()),
                Sort.by("id"));

        //then - not found, default matcher included the primary key
        then(foundBooks).isNotEmpty();
        then(foundBooks.get(0).getId()).isEqualTo(savedBook.getId());
    }

    /**
     * This example shows how we can ignore certain example properties. In this
     * case we are ignoring a built-in type that can never be null.
     */
    @Test
    void ignore_properties() {
        //given
        Book savedBook = savedBooks.get(0);
        Book probe = Book.builder()
                .title(savedBook.getTitle())
                .author(savedBook.getAuthor())
                .build();
        ExampleMatcher ignoreId = ExampleMatcher.matchingAll().withIgnorePaths("id");

        //when
        List<Book> foundBooks = booksRepository.findAll(
                Example.of(probe, ignoreId),
                Sort.by("id"));

        //then
        then(foundBooks).isNotEmpty();
        then(foundBooks.get(0).getId()).isEqualTo(savedBook.getId());
    }

    @Test
    void like_matcher() {
        //given
        Book savedBook = savedBooks.get(0);
        Book probe = Book.builder()
                .title(savedBook.getTitle().substring(2))
                .author(savedBook.getAuthor())
                .build();
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnorePaths("id")
                .withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains());

        //when
        List<Book> foundBooks = booksRepository.findAll(Example.of(probe, matcher), Sort.by("id"));

        //then
        then(foundBooks).isNotEmpty();
        then(foundBooks.get(0).getId()).isEqualTo(savedBook.getId());
    }
}
