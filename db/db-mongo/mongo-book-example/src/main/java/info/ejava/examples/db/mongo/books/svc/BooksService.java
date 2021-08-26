package info.ejava.examples.db.mongo.books.svc;

import info.ejava.examples.db.mongo.books.dto.BookDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface BooksService {
    BookDTO createBook(BookDTO bookDTO);
    BookDTO getBook(String id);
    BookDTO getRandomBook();
    void updateBook(String id, BookDTO bookDTO);
    void deleteBook(String id);
    void deleteAllBooks();

    Page<BookDTO> getBooks(Pageable pageable);
    Page<BookDTO> findPublishedAfter(LocalDate exclusive, Pageable pageable);
    Page<BookDTO> findBooksMatchingAll(BookDTO probe, Pageable pageable);
}
