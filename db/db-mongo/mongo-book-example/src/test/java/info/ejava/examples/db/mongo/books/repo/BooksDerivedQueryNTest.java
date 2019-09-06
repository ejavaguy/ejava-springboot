package info.ejava.examples.db.mongo.books.repo;

import info.ejava.examples.db.mongo.books.NTestConfiguration;
import info.ejava.examples.db.mongo.books.TestProfileResolver;
import info.ejava.examples.db.mongo.books.bo.Book;
import info.ejava.examples.db.mongo.books.dto.BookDTOFactory;
import info.ejava.examples.db.mongo.books.svc.BooksMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes = {NTestConfiguration.class})
@ActiveProfiles(resolver = TestProfileResolver.class)
@Tag("springboot")
@DisplayName("Repository Derived Query Methods")
@Slf4j
public class BooksDerivedQueryNTest {
    @Autowired
    private BooksRepository booksRepository;
    @Autowired
    private BookDTOFactory dtoFactory;
    @Autowired
    private BooksMapper mapper;
    @Value("${spring.data.mongodb.uri:embedded}")
    private String mongoUrl;

    private static List<Book> savedBooks = new ArrayList<>();

    @BeforeEach
    void populate() {
        log.info("mongoUrl={}", mongoUrl);
        if (savedBooks.isEmpty()) {
            booksRepository.deleteAll();
            IntStream.range(0, 3).forEach(i -> {
                Book book = mapper.map(dtoFactory.make(BookDTOFactory.nextDate));
                if (i == 2) {
                    book.setTitle(null);
                }
                booksRepository.save(book);
                savedBooks.add(book);
                //log.info("{}", book);
            });
        }
    }

    @Test
    void optional_exists() {
        //given
        Book book = savedBooks.stream().filter(s->s.getTitle()!=null).findFirst().get();

        //when
        Optional<Book> result = booksRepository.getByTitle(book.getTitle());
        //select ...
        // from repobooks_book book0_
        // where book0_.title=?
        log.info("title with '{}' {} find instance", book.getTitle(), result.isPresent()?"did":"did not");

        //then
        then(result.isPresent()).isTrue();
        Book foundSong = result.get();
        then(foundSong.getId()).isEqualTo(book.getId());
    }

    @Test
    void optional_does_not_exist() {
        //given
        String nonExistantTitle = "1234567890";

        //when
        Optional<Book> result = booksRepository.getByTitle(nonExistantTitle);
        log.info("title with '{}' {} find instance", nonExistantTitle, result.isPresent()?"did":"did not");

        //then
        then(result.isPresent()).isFalse();
        assertThatThrownBy(() -> result.get())
                .isInstanceOf(NoSuchElementException.class);

        //when
        Book foundSong = result.orElse(null);

        //then
        then(foundSong).isNull();
    }

    private Map.Entry<String, Long> getStartsWith() {
        Map<String, Long> counts = savedBooks.stream()
                .filter(s -> s.getTitle() != null)
                .collect(Collectors.groupingBy(s -> s.getTitle().substring(0, 1), Collectors.counting()));
        long maxCount = counts.values().stream().mapToLong(v->v).max().orElse(0);
        Map.Entry<String, Long> startsWith = counts.entrySet().stream()
                .filter(e -> maxCount == e.getValue())
                .findFirst().orElse(null);
        assertThat(startsWith).isNotNull().describedAs("no book found");
        assertThat(startsWith.getKey()).isNotNull();
        assertThat(startsWith.getValue()).isNotZero();
        return startsWith;
    }

    @Test
    void findBy_list() {
        //given
        Map.Entry<String, Long> startsWith = getStartsWith();
        String startingWith = startsWith.getKey();
        long expectedCount = startsWith.getValue();

        //when
        Sort sort = Sort.by("id").ascending();
        List<Book> books = booksRepository.findByTitleStartingWith(startingWith, sort);
        //select ...
        // from repobooks_book book0_
        // where book0_.title like ? escape ?
        // order by book0_.id asc

        //then
        then(books.size()).isEqualTo(expectedCount);
    }

    @Test
    void findBy_pagable_slice() {
        //given
        Map.Entry<String, Long> startsWith = getStartsWith();
        String startingWith = startsWith.getKey();

        //when
        PageRequest pageable = PageRequest.of(0, 1, Sort.by("id").ascending());
        Slice<Book> booksSlice = booksRepository.findByTitleStartingWith(startingWith, pageable);
        //select ...
        // from repobooks_book book0_
        // where book0_.title like ? escape ?
        // order by book0_.id asc limit ?

        //then
        then(booksSlice.getNumberOfElements()).isEqualTo(pageable.getPageSize());
    }

    @Test
    void findBy_pagable_page() {
        //given
        Map.Entry<String, Long> startsWith = getStartsWith();
        String startingWith = startsWith.getKey();
        long expectedCount = startsWith.getValue();

        //when
        PageRequest pageable = PageRequest.of(0, 1, Sort.by("id").ascending());
        Page<Book> booksPage = booksRepository.findPageByTitleStartingWith(startingWith, pageable);
        //select book0_.id as id1_0_, book0_.artist as artist2_0_, book0_.released as released3_0_, book0_.title as title4_0_
        //  from repobooks_book book0_
        //  where book0_.title like ? escape ?
        //  order by book0_.id asc
        //  limit ?
        //select count(book0_.id) as col_0_0_
        //  from repobooks_book book0_
        //  where book0_.title like ? escape ?

        //then
        then(booksPage.getNumberOfElements()).isEqualTo(pageable.getPageSize());
        then(booksPage.getTotalElements()).isEqualTo(expectedCount);
    }

    @Test
    void findBy_property_value() {
        //given
        Book book = savedBooks.stream().filter(s->s.getTitle()!=null).findFirst().get();

        //when
        List<Book> foundSongs = booksRepository.findByTitle(book.getTitle());
        log.info("title with '{}' found {}", book.getTitle(), foundSongs);

        //then
        then(foundSongs).hasSize(1);
        Book foundSong = foundSongs.get(0);
        then(foundSong.getId()).isEqualTo(book.getId());
    }

    @Test
    void findBy_property_not_value_does_not_account_for_not_exist() {
        //given
        Book book = savedBooks.stream().filter(s->s.getTitle()!=null).findFirst().get();
        Set<String> expectedIds = savedBooks.stream()
                .filter(s->s.getTitle()==null || !book.getTitle().equals(s.getTitle()))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Book> foundSongs = booksRepository.findByTitleNot(book.getTitle());
        log.info("title not '{}' found {}", book.getTitle(), foundSongs);

        //then - we end up getting nulls as well as existing values that do not equal
        Set<String> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(expectedIds);
    }

    @Test
    void findBy_property_null_value() {
        //given - a book with a null title
        Book book = savedBooks.stream().filter(s->s.getTitle()==null).findFirst().get();

        //when - query will look for "is null"
        List<Book> foundSongs = booksRepository.findByTitle(book.getTitle());
        log.info("title with '{}' found {}", book.getTitle(), foundSongs);

        //then - book with null title is found
        Book foundSong = foundSongs.get(0);
        then(foundSong.getId()).isEqualTo(book.getId());
    }

    @Test
    void findBy_contains() {
        //given
        Book book = savedBooks.stream().filter(s->s.getTitle()!=null).findFirst().get();
        String substring = book.getTitle().substring(1,5);
        Set<String> expectedIds = savedBooks.stream()
                .filter(s->s.getTitle()!=null && s.getTitle().contains(substring))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Book> foundSongs = booksRepository.findByTitleContaining(substring);
        log.info("title containing '{}' found {}", substring, foundSongs);

        //then
        Set<String> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(expectedIds);
    }

    @Test
    void findBy_not_contains() {
        //given
        Book book = savedBooks.stream().filter(s->s.getTitle()!=null).findFirst().get();
        String substring = book.getTitle().substring(1,5);
        //Spring Data Mongo will find nulls as well as != string
        Set<String> expectedIds = savedBooks.stream()
                .filter(s->s.getTitle()==null || !s.getTitle().contains(substring))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Book> foundSongs = booksRepository.findByTitleNotContaining(substring);
        log.info("title not containing '{}' found {}", substring, foundSongs);

        //then
        Set<String> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(expectedIds);
    }


    @Test
    void findBy_matches() {
        //given
        String title = savedBooks.stream().filter(s->s.getTitle()!=null).findFirst().get().getTitle();
        String regexPattern = "^"+ title.substring(0,1) + ".*" + title.substring(title.length()-1,title.length())+"$";
        Set<String> expectedIds = savedBooks.stream()
                .filter(s->s.getTitle()!=null && s.getTitle().matches(regexPattern))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Book> foundSongs = booksRepository.findByTitleMatches(regexPattern);
        log.info("\n{}", savedBooks.stream()
                .map(b->String.format("%s: %s\n", b.getId(), b.getTitle()))
                .collect(Collectors.joining()));
        log.info("title matches '{}' found {}", regexPattern, foundSongs);

        //then
        Set<String> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(expectedIds);
    }

    @Test
    void findBy_not_regexp_matches() {
        //given
        String title = savedBooks.stream().filter(s->s.getTitle()!=null).findFirst().get().getTitle();
        String regexPattern = "^"+title.substring(0,1) + ".*" + title.substring(title.length()-1,title.length())+"$";
        Set<String> expectedIds = savedBooks.stream()
                .filter(s->s.getTitle()==null || !s.getTitle().matches(regexPattern))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        /**
         * Had is use the @Query notation when using Not
         */
        //when
        List<Book> foundSongs = booksRepository.findByTitleNotMatches(regexPattern);
        log.info("title not match '{}' found {}", regexPattern, foundSongs);

        //then
        Set<String> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(expectedIds);
    }

    @Test
    void findByAfter() {
        //given - a middle book
        Book firstSong = savedBooks.get(1);
        Set<String> expectedIds = savedBooks.stream()
                .filter(s->s.getPublished().isAfter(firstSong.getPublished()))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Book> foundSongs = booksRepository.findByPublishedAfter(firstSong.getPublished());
        log.info("released after '{}' found {}", firstSong.getPublished(), foundSongs);

        //then
        Set<String> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(expectedIds);
    }

    @Test
    void findByGreaterThanEqual() {
        //given - a middle book
        Book firstSong = savedBooks.get(1);
        Set<String> expectedIds = savedBooks.stream()
                .filter(s->!s.getPublished().isBefore(firstSong.getPublished()))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Book> foundSongs = booksRepository.findByPublishedGreaterThanEqual(firstSong.getPublished());
        log.info("released GE '{}' found {}", firstSong.getPublished(), foundSongs);

        //then
        Set<String> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(expectedIds);
    }


    @Test
    void findBy_multiple_predicates() {
        Book firstSong = savedBooks.get(0);
        Set<String> expectedIds = savedBooks.stream()
                .filter(s->s.getTitle()==null)
                .filter(s->s.getPublished().isAfter(firstSong.getPublished()))
                .map(s->s.getId())
                .collect(Collectors.toSet());

        //when
        List<Book> foundSongs = booksRepository.findByTitleNullAndPublishedAfter(firstSong.getPublished());
        //select ...
        // from repobooks_book book0_
        // where (book0_.title is null) and book0_.released>?
        log.info("title null and released after '{}' found {}", firstSong.getPublished(), foundSongs);

        //then
        Set<String> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
        then(foundIds).isEqualTo(expectedIds);
    }

}
