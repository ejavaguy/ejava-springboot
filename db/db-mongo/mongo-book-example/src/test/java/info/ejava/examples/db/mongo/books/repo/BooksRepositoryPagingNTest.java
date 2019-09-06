package info.ejava.examples.db.mongo.books.repo;

import info.ejava.examples.db.mongo.books.TestProfileResolver;
import info.ejava.examples.db.mongo.books.dto.BookDTO;
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
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static info.ejava.examples.db.mongo.books.dto.BookDTOFactory.nextDate;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes= NTestConfiguration.class)
@Tag("springboot")
@ActiveProfiles(resolver = TestProfileResolver.class)
//@ActiveProfiles(profiles="mongodb", resolver = TestProfileResolver.class)
@DisplayName("Repository Sorting/Paging")
@Slf4j
public class BooksRepositoryPagingNTest {
    @Autowired
    private BooksRepository booksRepository;
    @Autowired
    private BookDTOFactory dtoFactory;
    @Autowired
    private BooksMapper mapper;

    private String titlePrefix = "123";
    private UnaryOperator<BookDTO> addTitlePrefix = s->{
        s.setTitle(titlePrefix + s.getTitle());
        return s;
    };
    private List<Book> savedBooks = new ArrayList<>();

    @BeforeEach
    void populate() {
        booksRepository.deleteAll();
        IntStream.range(0,10).forEach(i->{
            Book book = mapper.map(dtoFactory.make(nextDate, addTitlePrefix));
            savedBooks.add(book);
        });
        booksRepository.saveAll(savedBooks);
    }

    @Test
    void findAll_sorted() {
        //when
        List<Book> byPublished = booksRepository.findAll(
                Sort.by("published").descending().and(Sort.by("id").ascending()));
        log.info("ordered by published date DSC found {}", byPublished);

        //then
        LocalDate previous = null;
        for (Book s: byPublished) {
            if (previous!=null) {
                then(previous).isAfterOrEqualTo(s.getPublished()); //DESC order
            }
            previous=s.getPublished();
        }
    }

    @Test
    void findAll_sorted_and_paged() {
        //given
        int offset = 0;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(offset / pageSize, pageSize, Sort.by("published"));
        //Pageable next = pageable.next();
        //Pageable previous = pageable.previousOrFirst();
        //Pageable first = pageable.first();

        //when
        Page<Book> bookPage = booksRepository.findAll(pageable);

        //then
        Slice bookSlice = bookPage;
        then(bookSlice).isNotNull();
        then(bookSlice.isEmpty()).isFalse();
        then(bookSlice.getNumber()).isEqualTo(0);
        then(bookSlice.getSize()).isEqualTo(pageSize);
        then(bookSlice.getNumberOfElements()).isEqualTo(pageSize);

        then(bookPage.getTotalElements()).isEqualTo(savedBooks.size());

        List<Book> booksList = bookSlice.getContent();
        then(booksList).hasSize(pageSize);

        for (int i=1; bookSlice.hasNext(); i++) {
            pageable = pageable.next();
            bookSlice = booksRepository.findAll(pageable);
            booksList = bookSlice.getContent();
            then(bookSlice).isNotNull();
            then(bookSlice.getNumber()).isEqualTo(i);
            then(bookSlice.getSize()).isLessThanOrEqualTo(pageSize);
            then(bookSlice.getNumberOfElements()).isLessThanOrEqualTo(pageSize);
            then(((Page)bookSlice).getTotalElements()).isEqualTo(savedBooks.size()); //unique to Page
        }
        then(bookSlice.hasNext()).isFalse();
        then(bookSlice.getNumber()).isEqualTo(booksRepository.count() / pageSize);
    }

    @Test
    void sorting() {
        //when
        List<String> dbIdsByTitleASC = booksRepository.findByTitleStartingWith(titlePrefix, Sort.by("published").ascending())
                .stream()
                .map(s->s.getId())
                .collect(Collectors.toList());
        log.info("ordered by published date ASC found {}", dbIdsByTitleASC);

        //then
        List<String> idByTitleASC = savedBooks.stream()
                .sorted(Comparator.comparing(Book::getPublished))
                .map(s->s.getId())
                .collect(Collectors.toList());
        then(dbIdsByTitleASC).isEqualTo(idByTitleASC);

        //when
        List<String> dbIdsByTitleDSC = booksRepository.findByTitleStartingWith(titlePrefix, Sort.by("published").descending())
                .stream()
                .map(s->s.getId())
                .collect(Collectors.toList());
        log.info("ordered by published date DSC found {}", dbIdsByTitleDSC);

        //then
        List<String> idByTitleDSC = savedBooks.stream()
                .sorted(Comparator.comparing(Book::getPublished, Comparator.reverseOrder()))
                .map(s->s.getId())
                .collect(Collectors.toList());
        then(dbIdsByTitleDSC).isEqualTo(idByTitleDSC);
    }


    @Test
    void paging_slice() {
        //given
        int offset = 0;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(offset / pageSize, pageSize, Sort.by("published"));
        Set<String> ids = savedBooks.stream().map(s -> s.getId()).collect(Collectors.toSet());

        //when
        Slice<Book> bookPage = booksRepository.findByTitleStartingWith(titlePrefix, pageable);

        //then
        then(bookPage).isNotNull();
        then(bookPage.isEmpty()).isFalse();
        then(bookPage.getNumber()).isEqualTo(0);
        then(bookPage.getSize()).isEqualTo(pageSize);
        then(bookPage.getNumberOfElements()).isEqualTo(pageSize);

        List<Book> booksList = bookPage.getContent();
        then(booksList.size()).isEqualTo(pageSize);
        then(booksList).allMatch(s->ids.remove(s.getId()));

        for (int i=1; bookPage.hasNext(); i++) {
            pageable = pageable.next();
            bookPage = booksRepository.findByTitleStartingWith(titlePrefix, pageable);
            booksList = bookPage.getContent();
            then(bookPage).isNotNull();
            then(bookPage.getNumber()).isEqualTo(i);
            then(bookPage.getSize()).isLessThanOrEqualTo(pageSize);
            then(bookPage.getNumberOfElements()).isLessThanOrEqualTo(pageSize);
            then(booksList).allMatch(s->ids.remove(s.getId()));
        }
        then(bookPage.hasNext()).isFalse();
        then(bookPage.getNumber()).isEqualTo(booksRepository.count() / pageSize);
        then(ids).isEmpty();
    }

    @Test
    void paging_pageable() {
        //given
        int offset = 0;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(offset / pageSize, pageSize, Sort.by("published"));
        Set<String> ids = savedBooks.stream().map(s -> s.getId()).collect(Collectors.toSet());

        //when
        Page<Book> bookPage = booksRepository.findPageByTitleStartingWith(titlePrefix, pageable);

        //then
        then(bookPage).isNotNull();
        then(bookPage.isEmpty()).isFalse();
        then(bookPage.getNumber()).isEqualTo(0);
        then(bookPage.getSize()).isEqualTo(pageSize);
        then(bookPage.getNumberOfElements()).isEqualTo(pageSize);
        then(bookPage.hasNext()).isTrue();

        then(bookPage.getTotalElements()).isEqualTo(savedBooks.size());
        then(bookPage.getTotalPages()).isEqualTo(savedBooks.size() / pageSize + (savedBooks.size() % pageSize==0?0:1));
    }
}
