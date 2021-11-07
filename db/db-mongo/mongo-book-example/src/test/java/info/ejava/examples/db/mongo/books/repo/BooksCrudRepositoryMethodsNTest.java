package info.ejava.examples.db.mongo.books.repo;

import info.ejava.examples.db.mongo.books.TestProfileResolver;
import info.ejava.examples.db.mongo.books.NTestConfiguration;
import info.ejava.examples.db.mongo.books.bo.Book;
import info.ejava.examples.db.mongo.books.dto.BookDTOFactory;
import info.ejava.examples.db.mongo.books.svc.BooksMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes = {NTestConfiguration.class})
@ActiveProfiles(resolver = TestProfileResolver.class)
@Tag("springboot")
@Slf4j
@DisplayName("Repository Crud Methods")
class BooksCrudRepositoryMethodsNTest {
    @Autowired
    private BooksRepository booksRepo;
    @Autowired
    private BooksMapper mapper;
    @Autowired
    private BookDTOFactory dtoFactory;
    @Value("${spring.data.mongodb.uri:embedded}")
    private String dbUrl;

    @BeforeEach
    void setUp() {
        log.info("dbUrl={}", dbUrl);
    }

    @BeforeEach
    @AfterEach
    void cleanup() {
        booksRepo.deleteAll();
    }

    @Test
    void save_new() {
        //given a transient document instance
        Book book = mapper.map(dtoFactory.make());
        assertThat(book.getId()).isNull();

        //when persisting
        booksRepo.save(book);
        //insertOne({ ... })

        //then document is persisted
        then(book.getId()).isNotNull();
        log.info("{}", book);
    }

    @Test
    void save_update() {
        //given a persisted document instance
        Book book = mapper.map(dtoFactory.make());
        booksRepo.save(book);
        Book updatedBook = book.withTitle("new title");

        //when persisting update
        booksRepo.save(updatedBook);
        // update{"q":{"_id":{"$oid":"606cbfc0932e084392422bb6"}},
        // "u":{"_id":{"$oid":"606cbfc0932e084392422bb6"},"title":"new title","author":...},
        // "multi":false,
        // "upsert":true}

        //then new document state is persisted
        then(booksRepo.findOne(Example.of(updatedBook))).isPresent();
    }

    @Test
    void exists() {
        //given a persisted document instance
        Book pojoBook = mapper.map(dtoFactory.make());
        booksRepo.save(pojoBook);

        //when - determining if document exists
        boolean exists = booksRepo.existsById(pojoBook.getId());
        //query: { _id: ObjectId('606cc5d742931870e951e08e') }
        // sort: {} projection: {} collation: { locale: \"simple\" }
        // limit: 1"}}

        //then
        then(exists).isTrue();
    }

    @Test
    void findById_found() {
        //given a persisted document instance
        Book pojoBook = mapper.map(dtoFactory.make());
        booksRepo.save(pojoBook);

        //when - finding the existing document
        Optional<Book> result = booksRepo.findById(pojoBook.getId());
        //query: { _id: ObjectId('606cc62e5c6e546682f45d80') }
        // sort: {} projection: {} limit: 1"}}

        //then
        then(result.isPresent()).isTrue();
        then(result).isPresent();

        //when - obtaining the instance
        Book dbBook = result.get();

        //then - database copy matches initial POJO
        then(dbBook).isNotNull();
        then(dbBook.getAuthor()).isEqualTo(pojoBook.getAuthor());
        then(dbBook.getTitle()).isEqualTo(pojoBook.getTitle());
        then(pojoBook.getPublished()).isEqualTo(dbBook.getPublished());
    }

    @Test
    void findById_not_found() {
        //given - an ID that does not exist
        String missingId = "123456";

        //when - using find for a missing ID
        Optional<Book> result = booksRepo.findById(missingId);

        //then - the optional can be benignly tested
        then(result).isNotPresent();

        //then - the optional is asserted during the get()
        assertThatThrownBy(() -> result.get())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void saveAll_entities() {
        //given - several books persisted
        Collection<Book> books = dtoFactory.listBuilder().books(3, 3).stream()
                .map(dto->mapper.map(dto))
                .collect(Collectors.toList());

        //when
        booksRepo.saveAll(books);

        //then - each will exist in the DB
        books.stream().forEach(s->{
            booksRepo.existsById(s.getId());
        });
    }


    @Test
    void findAll_entities() {
        //given - several books persisted
        Collection<Book> pojoBooks = dtoFactory.listBuilder().books(3, 3).stream()
                .map(dto->mapper.map(dto))
                .collect(Collectors.toList());
        booksRepo.saveAll(pojoBooks);
        Map<String, Book> pojoBooksMap = pojoBooks.stream()
                .collect(Collectors.toMap(s->s.getId(), s->s));

        //when
        Iterable<Book> result = booksRepo.findAll();
        //query: {} sort: {} projection: {}

        //then - we can find each instance
        then(result).hasSameSizeAs(pojoBooks);
        then(result).allMatch(s->pojoBooksMap.containsKey(s.getId()));
    }

    @Test
    void delete_exists() {
        //given - a persisted document instance
        Book existingBook = mapper.map(dtoFactory.make());
        booksRepo.save(existingBook);

        //when - deleting an existing instance
        booksRepo.delete(existingBook);
        // {"q":{"_id":{"$oid":"606cc9ec05399f7256d48ff0"}},"limit":1}

        //then - instance will be removed from DB
        then(booksRepo.existsById(existingBook.getId())).isFalse();
    }

    @Test
    void delete_not_exists() {
        //given - a persisted document instance
        Book doesNotExist = mapper.map(dtoFactory.make(BookDTOFactory.oneUpId));
        then(booksRepo.existsById(doesNotExist.getId())).isFalse();

        //when - deleting a non-existing instance
        booksRepo.delete(doesNotExist);
        //{"q":{"_id":"1"},"limit":1}
    }

    @Test
    void deleteById_exists() {
        //given - a persisted document instance
        Book existingBook = mapper.map(dtoFactory.make());
        booksRepo.save(existingBook);

        //when - deleting an existing instance
        booksRepo.deleteById(existingBook.getId());

        //then - instance will be removed from DB
        then(booksRepo.existsById(existingBook.getId())).isFalse();
    }

    @Test
    void deleteById_not_exists() {
        //given - an ID that does not exist
        String missingId = "123456";

        //when - deleting an non-existant instance
        Throwable ex= catchThrowable(()->{
            booksRepo.deleteById(missingId);
        });

        //then - no exception was ever thrown
        log.info("{}", ex);
        //then(ex).isInstanceOf(EmptyResultDataAccessException.class);
        then(ex).isNull();
    }

    @Test
    void deleteAll_every() {
        //given
        Collection<Book> pojoBooks = dtoFactory.listBuilder().books(3, 3).stream()
                .map(dto->mapper.map(dto))
                .collect(Collectors.toList());
        booksRepo.saveAll(pojoBooks);

        //when
        booksRepo.deleteAll();

        //then
        then(pojoBooks).allSatisfy(s-> then(booksRepo.existsById(s.getId())).isFalse());
    }

    @Test
    void deleteAll_some() {
        //given
        List<Book> pojoBooks = dtoFactory.listBuilder().books(3, 3).stream()
                .map(dto->mapper.map(dto))
                .collect(Collectors.toList());
        booksRepo.saveAll(pojoBooks);
        List<Book> toDelete = IntStream.range(0,2)
                .mapToObj(i->pojoBooks.get(i))
                .collect(Collectors.toList());

        //when - deleting a subset
        booksRepo.deleteAll(toDelete);

        //then
        then(booksRepo.existsById(pojoBooks.get(0).getId())).isFalse();
        then(booksRepo.existsById(pojoBooks.get(1).getId())).isFalse();
        then(booksRepo.existsById(pojoBooks.get(2).getId())).isTrue();
    }

    @Test
    void count() {
        //given
        List<Book> pojoBooks = dtoFactory.listBuilder().books(3, 3).stream()
                .map(dto->mapper.map(dto))
                .collect(Collectors.toList());
        booksRepo.saveAll(pojoBooks);

        //when
        long bookCount = booksRepo.count();

        //then
        then(bookCount).isEqualTo(pojoBooks.size());
    }

    @Test
    void save_modify_existing() {
        //given - a persisted document instance
        Book book = mapper.map(dtoFactory.make());
        booksRepo.save(book);
        String originalTitle = book.getTitle();
        String modifiedTitle = dtoFactory.title()+UUID.randomUUID();
        assertThat(originalTitle).isNotEqualTo(modifiedTitle);

        //when - modifying book instance without saving
        book.setTitle(modifiedTitle);

        //then - DB is not modified without an explicit save
        Book dbBook = booksRepo.findById(book.getId()).get();
        then(dbBook.getTitle()).isEqualTo(originalTitle);

        //when - book is explicitly updated
        booksRepo.save(book);
        //then
        dbBook = booksRepo.findById(book.getId()).get();
        then(dbBook.getTitle()).isEqualTo(modifiedTitle);
    }
}
