package info.ejava.examples.db.mongo.books.repo;

import info.ejava.examples.db.mongo.books.NTestConfiguration;
import info.ejava.examples.db.mongo.books.TestProfileResolver;
import info.ejava.examples.db.mongo.books.bo.Book;
import info.ejava.examples.db.mongo.books.dto.BookDTOFactory;
import info.ejava.examples.db.mongo.books.svc.BooksMapper;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes= NTestConfiguration.class)
@ActiveProfiles(resolver = TestProfileResolver.class)
@Tag("springboot")
@Slf4j
public class BooksMongoRepositoryNTest {
    @Autowired
    private BooksRepository booksRepository;
    @Autowired
    private BookDTOFactory dtoFactory;
    @Autowired
    private BooksMapper mapper;

    @BeforeEach
    @AfterEach
    public void cleanup() {
        booksRepository.deleteAll();
    }

    /**
     * Standard save, will insert if new and update if exists. This has a small amount
     * of useful overhead to determine the correct action to take.
     */
    @Test
    public void generic_repo_save_performs_upsert() {
        //given
        Book book = mapper.map(dtoFactory.make());
        Set<String> ids = new HashSet<>();

        for (int i=0; i<3; i++) {
            //when - performs insert when no ID and upsert when ID assigned
            booksRepository.save(book);
            // when ID has been assigned
            // {"type":"update","ns":"test.books","command":{"q":{"_id":"1"},
            // "u":{"_id":"1","title":"The Way Through the Woods","author":"Laurette McGlynn","published":{"$date":"2019-01-29T05:00:00.000Z"},"
            // _class":"info.ejava.examples.db.mongo.books.bo.Book"},
            // "multi":false,"upsert":true}
            //then
            then(book.getId()).isNotNull();
            ids.add(book.getId());
        }
        //then
        then(ids).hasSize(1);
    }


    /**
     * Method claimed to have certain optimizations over save but must not already exist
     */
    @Test
    public void mongo_repo_insert() {
        //given
        Book book = mapper.map(dtoFactory.make());

        for(int i=0; i<3; i++) {
            //when
            DuplicateKeyException ex = BDDAssertions.catchThrowableOfType(
                    ()->{booksRepository.insert(book);},
                    DuplicateKeyException.class);
            //then
            if (i==0) {
                then(ex).isNull();
                then(book.getId()).isNotNull();
            } else {
                then(ex).isNotNull();
            }
        }
    }
}
