package info.ejava.examples.db.mongo.books.repo;

import info.ejava.examples.db.mongo.books.bo.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BooksRepository extends MongoRepository<Book, String>, BookRepositoryCustom {
    Optional<Book> getByTitle(String title);

    List<Book> findByTitle(String title);
    List<Book> findByTitleNot(String title);
    List<Book> findByTitleContaining(String string);
    List<Book> findByTitleNotContaining(String title);
    List<Book> findByTitleMatches(String string);
    /* NotMatches would not generate a valid query -- needed to manually define */
//    @Query("{ 'title' : { $not : { $regex: ?0} } }")
    @Query("{ 'title' : { $not : /?0/ } }")
    List<Book> findByTitleNotMatches(String title);

    List<Book> findByPublishedAfter(LocalDate date);
    List<Book> findByPublishedGreaterThanEqual(LocalDate date);
    /* Between was generating an exclusive search */
    @Query("{ 'published': { $gte: ?0, $lte: ?1 } }")
    List<Book> findByPublishedBetween(LocalDate starting, LocalDate ending);

    Page<Book> findByPublishedAfter(LocalDate date, Pageable pageable);

    List<Book> findByTitleNullAndPublishedAfter(LocalDate date);
    Slice<Book> findByTitleNullAndPublishedAfter(LocalDate date, Pageable pageable);
    Page<Book> findPageByTitleNullAndPublishedAfter(LocalDate date, Pageable pageable);

//    @Query(value="{ 'title': {$exists:true}, $where: 'this.title.length >= ?0' }", fields="{'_id':0, 'title':1}")
    @Query(value="{ 'title': /^.{?0,}$/ }", fields="{'_id':0, 'title':1}")
    List<Book> getTitlesGESizeAsBook(int length);

    List<Book> findByTitleStartingWith(String string, Sort sort);
    Slice<Book> findByTitleStartingWith(String string, Pageable pageable);
    Page<Book> findPageByTitleStartingWith(String string, Pageable pageable);

}
