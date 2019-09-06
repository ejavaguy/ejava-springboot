package info.ejava.examples.db.mongo.books.repo;

import info.ejava.examples.db.mongo.books.bo.Book;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepositoryCustom {
    Optional<Book> random();

    List<String> findByTitleGESizeAsString(int length);
    List<Book> findByAuthorGESize(int length);
}
