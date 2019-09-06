package info.ejava.examples.db.mongo.books.mongoops;

import com.mongodb.client.MongoClient;
import info.ejava.examples.db.mongo.books.TestProfileResolver;
import info.ejava.examples.db.mongo.books.dao.MongoOpsBookDAO;
import info.ejava.examples.db.mongo.books.bo.Book;
import info.ejava.examples.db.mongo.books.NTestConfiguration;
import info.ejava.examples.db.mongo.books.dto.BookDTOFactory;
import info.ejava.examples.db.mongo.books.svc.BooksMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static info.ejava.examples.db.mongo.books.dto.BookDTOFactory.nextDate;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes= NTestConfiguration.class)
@ActiveProfiles(resolver = TestProfileResolver.class)
//@ActiveProfiles(profiles="mongodb", resolver = TestProfileResolver.class)
@Tag("springboot")
@Slf4j
public class MongoOpsBooksNTest {
    @Autowired
    private MongoOpsBookDAO mongoDao;
    @Autowired
    private BookDTOFactory dtoFactory;
    @Autowired
    private BooksMapper mapper;
    @Autowired
    private MongoClient mongoClient;
    @Value("${spring.data.mongodb.uri:embedded}")
    private String mongoUrl;


    @BeforeEach
    void setUp() {
        log.info("dbUrl={}", mongoUrl);
    }
    @BeforeEach
    @AfterEach
    void cleanup() throws SQLException {
        mongoDao.deleteAll();
    }

    @Test
    void create() throws SQLException {
        //given an entity instance
        Book book = mapper.map(dtoFactory.make());

        //when persisting
        mongoDao.create(book);

        //then document is persisted
        then(book.getId()).isNotNull();
        then(mongoDao.existsById(book.getId())).isTrue();
    }

    @Test
    void create_already_exists() throws SQLException {
        //given a persisted instance
        Book book = mapper.map(dtoFactory.make());
        mongoDao.create(book);

        //when persisting an instance by the same ID
        Assertions.assertThrows(DuplicateKeyException.class,
                ()->mongoDao.create(book));
    }

    @Test
    void exists_exists() throws SQLException {
        //given a persisted instance
        Book book = mapper.map(dtoFactory.make());
        mongoDao.create(book);

        //when testing exists
        boolean exists = mongoDao.existsById(book.getId());

        //then document exists
        then(exists).isTrue();
    }

    @Test
    void findById_exists() throws SQLException {
        //given a persisted instance
        Book book = mapper.map(dtoFactory.make());
        mongoDao.create(book);

        //when finding
        Book dbBook = mongoDao.findById(book.getId());

        //then document is found
        then(dbBook.getId()).isEqualTo(book.getId());
        then(dbBook.getTitle()).isEqualTo(book.getTitle());
        then(dbBook.getAuthor()).isEqualTo(book.getAuthor());
        then(dbBook.getPublished()).isEqualTo(book.getPublished());
    }


    @Test
    void findById_does_not_exist() throws SQLException {
        //given an ID not in the DB
        String missingId = "12345";

        //when finding
        Book dbBook = mongoDao.findById(missingId);
        //then
        then(dbBook).isNull();
    }

    @Test
    void update_exists() throws SQLException {
        //given a persisted instance
        Book originalBook = mapper.map(dtoFactory.make());
        mongoDao.create(originalBook);
        Book updatedBook = mapper.map(dtoFactory.make()).withId(originalBook.getId());
        assertThat(updatedBook.getTitle()).isNotEqualTo(originalBook.getTitle());

        //when - updating
        mongoDao.update(updatedBook);

        //then - db has new state
        Book dbBook = mongoDao.findById(originalBook.getId());
        then(dbBook.getTitle()).isEqualTo(updatedBook.getTitle());
        then(dbBook.getAuthor()).isEqualTo(updatedBook.getAuthor());
        then(dbBook.getPublished()).isEqualTo(updatedBook.getPublished());
    }

    @Test
    void update_does_not_exist() throws SQLException {
        //given a document not yet saved to DB
        Book transientBook = mapper.map(dtoFactory.make());
        assertThat(transientBook.getId()).isNull();

        //when - updating
        mongoDao.update(transientBook);

        //then - db has new state
        then(transientBook.getId()).isNotNull();
        Book dbBook = mongoDao.findById(transientBook.getId());
        then(dbBook.getTitle()).isEqualTo(transientBook.getTitle());
        then(dbBook.getAuthor()).isEqualTo(transientBook.getAuthor());
        then(dbBook.getPublished()).isEqualTo(transientBook.getPublished());
    }

    @Test
    void update_title_exists() throws SQLException {
        //given a persisted instance
        Book originalBook = mapper.map(dtoFactory.make());
        mongoDao.create(originalBook);
        String newTitle = "X" + originalBook.getTitle();

        //when - updating
        then(mongoDao.existsById(originalBook.getId())).isTrue();
        long found = mongoDao.updateTitle(originalBook.getId(), newTitle);
        //{ "_id" : { "$oid" : "60858ca8a3b90c12d3bb15b2"}} ,
        //{ "$set" : { "title" : "XTo Sail Beyond the Sunset"}}

        //then - db has new state
        then(found).isEqualTo(1);
        Book dbBook = mongoDao.findById(originalBook.getId());
        then(dbBook.getTitle()).isEqualTo(newTitle);
        then(dbBook.getAuthor()).isEqualTo(originalBook.getAuthor());
        then(dbBook.getPublished()).isEqualTo(originalBook.getPublished());
    }

    @Test
    void delete_exists() throws SQLException {
        //given a persisted instance
        Book book = mapper.map(dtoFactory.make());
        mongoDao.create(book);

        //when - deleting
        long count = mongoDao.delete(book);

        //then - no longer in DB
        then(count).isEqualTo(1);
        then(mongoDao.existsById(book.getId())).isFalse();
    }

    @Test
    void delete_does_not_exist() throws SQLException {
        //given a bad ID
        String missingID = "12345";
        Book missingBook = mapper.map(dtoFactory.make(b->{b.setId(missingID); return b;}));

        //when - deleting missing ID
        long count = mongoDao.delete(missingBook);
        //then - no exception
        then(count).isEqualTo(0);
    }

    @Test
    void delete_by_id_exists() throws SQLException {
        //given a persisted instance
        Book book = mapper.map(dtoFactory.make());
        mongoDao.create(book);

        //when - deleting
        long count = mongoDao.deleteById(book.getId());

        //then - no long in DB
        then(count).isEqualTo(1);
        then(mongoDao.existsById(book.getId())).isFalse();
    }

    @Test
    void delete_by_iddoes_not_exist() throws SQLException {
        //given a bad ID
        String missingID = "12345";

        //when - deleting missing ID
        long count = mongoDao.deleteById(missingID);
        //then - no exception
        then(count).isEqualTo(0);
    }

    @Nested
    class finders {
        private List<Book> savedBooks = new ArrayList<>();

        @BeforeEach
        void populate() {
            mongoDao.deleteAll();
            IntStream.range(0, 10).forEach(i -> {
                Book book = mapper.map(dtoFactory.make(nextDate));
                if (i % 3 == 0) {
                    book.setTitle(null);
                    book.setAuthor(null);
                }
                savedBooks.add(book);
                mongoDao.create(book);
            });
        }

        @Test
        void random(){
            //when
            Optional<Book> randomSong = mongoDao.random();

            //then
            then(randomSong.isPresent()).isTrue();
            then(randomSong.get()).isNotNull();
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
            List<Book> foundSongs = mongoDao.findByAuthorGESize(minLength);
            log.info("title size GE '{}' found {}", minLength, foundSongs);

            //then
            Set<String> foundIds = foundSongs.stream().map(s->s.getId()).collect(Collectors.toSet());
            then(foundIds).isEqualTo(ids);
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
            List<String> foundTitles = mongoDao.findByTitleGESizeAsString(minLength);
            log.info("title size GE '{}' found {}", minLength, foundTitles);

            //then
            then(new HashSet<>(foundTitles)).isEqualTo(titles);
        }

        @Test
        void sort_skip_limit() {
            //given
            List<String> orderedBooks = savedBooks.stream()
                    .sorted((p, c) -> p.getPublished().compareTo(c.getPublished()))
                    .map(b -> b.getId())
                    .collect(Collectors.toList());
            int offset=2;
            int limit=3;

            //when
            List<Book> books = mongoDao.find(Arrays.asList("published"), offset, limit);
            //then
            then(books).hasSize(limit);
            for (int i=0; i<limit; i++) {
                then(books.get(i).getId()).isEqualTo(orderedBooks.get(offset + i));
            }
        }

        @Test
        void pageable() {
            //given
            List<Object[]> orderedBooks = savedBooks.stream()
                    .sorted((p, c) -> c.getPublished().compareTo(p.getPublished()))
                    .map(b -> new Object[]{b.getId(), b.getPublished()})
                    .collect(Collectors.toList());
            int pageNo=1;
            int pageSize=3;
            Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "published"));

            //when
            List<Book> books = mongoDao.find(pageable);
            //then
            then(books).hasSize(pageSize);
            for (int i=pageSize-1; i>=0; i--) {
                then(books.get(i).getId()).isEqualTo(orderedBooks.get((pageNo*pageSize) + i)[0]);
            }
        }
    }
}
