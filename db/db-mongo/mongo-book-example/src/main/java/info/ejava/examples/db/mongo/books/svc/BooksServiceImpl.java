package info.ejava.examples.db.mongo.books.svc;

import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava.examples.db.mongo.books.bo.Book;
import info.ejava.examples.db.mongo.books.dto.BookDTO;
import info.ejava.examples.db.mongo.books.repo.BooksRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BooksServiceImpl implements BooksService {
    private final BooksMapper mapper;
    private final BooksRepository booksRepo;

    @Override
    public BookDTO createBook(BookDTO bookDTO) {
        Book bookBO = mapper.map(bookDTO);

        //insert instance
        booksRepo.save(bookBO);

        return mapper.map(bookBO);
    }

    @Override
    public BookDTO getBook(String id) {
        //leverage Optional features
        return booksRepo.findById(id)
                .map(bookBO->mapper.map(bookBO))
                .orElseThrow(()->new ClientErrorException.NotFoundException("Book id[%s] not found", id));
    }

    @Override
    public BookDTO getRandomBook() {
        //or test the optional manually
        Optional<Book> bookBO = booksRepo.random();
        if (!bookBO.isPresent()) {
            throw new ClientErrorException.NotFoundException("Random Book not found");
        }
        return mapper.map(bookBO.get());
    }

    @Override
    public void updateBook(String id, BookDTO bookDTO) {
        bookDTO.setId(id);
        Book bookBO=mapper.map(bookDTO);

        booksRepo.save(bookBO);
    }

    @Override
    public void deleteBook(String id) {
        booksRepo.deleteById(id);
    }

    @Override
    public Page<BookDTO> findPublishedAfter(LocalDate afterDate, Pageable pageable) {
        Page<Book> books = booksRepo.findByPublishedAfter(afterDate, pageable);
        return mapper.map(books);
    }

    @Override
    public void deleteAllBooks() {
        booksRepo.deleteAll();
    }


    @Override
    public Page<BookDTO> findBooksMatchingAll(BookDTO probeDTO, Pageable pageable) {
        Book probe = mapper.map(probeDTO);
        ExampleMatcher matcher = ExampleMatcher.matchingAll();
        Page<Book> books = booksRepo.findAll(Example.of(probe, matcher), pageable);
        return mapper.map(books);
    }
}
