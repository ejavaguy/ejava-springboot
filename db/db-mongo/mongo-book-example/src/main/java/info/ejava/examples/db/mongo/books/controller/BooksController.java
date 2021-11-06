package info.ejava.examples.db.mongo.books.controller;


import info.ejava.examples.common.web.paging.PageableDTO;
import info.ejava.examples.db.mongo.books.dto.BookDTO;
import info.ejava.examples.db.mongo.books.dto.BooksPageDTO;
import info.ejava.examples.db.mongo.books.svc.BooksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BooksController {
    public static final String BOOKS_PATH ="api/books";
    public static final String BOOK_PATH = BOOKS_PATH + "/{id}";
    public static final String RANDOM_BOOK_PATH = BOOKS_PATH + "/random";

    private final BooksService booksService;

    @RequestMapping(path= BOOKS_PATH,
            method= RequestMethod.POST,
            consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO bookDTO) {

        BookDTO result = booksService.createBook(bookDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .replacePath(BOOK_PATH)
                .build(result.getId());
        ResponseEntity<BookDTO> response = ResponseEntity.created(uri).body(result);
        return response;
    }

    @RequestMapping(path=BOOKS_PATH,
            method= RequestMethod.GET,
            produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BooksPageDTO> getSongs(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
            @RequestParam(value = "sort", required = false) String sortString
    ) {
        Pageable pageable = PageableDTO.of(pageNumber, pageSize, sortString).toPageable();
        Page<BookDTO> result = booksService.getBooks(pageable);

        BooksPageDTO resultDTO = new BooksPageDTO(result);
        ResponseEntity<BooksPageDTO> response = ResponseEntity.ok(resultDTO);
        return response;
    }

    @RequestMapping(path= BOOK_PATH,
            method=RequestMethod.GET,
            produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BookDTO> getSong(
            @PathVariable(name="id") String id) {

        BookDTO result = booksService.getBook(id);

        ResponseEntity<BookDTO> response = ResponseEntity.ok(result);
        return response;
    }

    @RequestMapping(path= RANDOM_BOOK_PATH,
            method=RequestMethod.GET,
            produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BookDTO> getRandomSong() {

        BookDTO result = booksService.getRandomBook();

        ResponseEntity<BookDTO> response = ResponseEntity.ok(result);
        return response;
    }

    @RequestMapping(path= BOOK_PATH,
            method=RequestMethod.PUT,
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> updateBook(
            @PathVariable("id") String id,
            @RequestBody BookDTO bookDTO) {

        booksService.updateBook(id, bookDTO);

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

    @RequestMapping(path= BOOK_PATH,
            method=RequestMethod.DELETE)
    public ResponseEntity<Void> deleteBook(
            @PathVariable(name="id") String id) {

        booksService.deleteBook(id);

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

    @RequestMapping(path= BOOKS_PATH,
            method=RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllBooks() {

        booksService.deleteAllBooks();

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

    @Operation(summary = "This endpoint will find matching Books based on a provided " +
            "example. The supplied Book example implements exact match only and ignores " +
            "the `id` field. This endpoint supports sorting and paging through request " +
            "query params. The results are returned in a page construct indicating the " +
            "content, total number of matching books, and the requested pageable properties. " +
            "This paging is 100% inline with Spring Data Pageable constructs.")
    @RequestMapping(path= BOOKS_PATH + "/example",
            method=RequestMethod.POST,
            consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<BooksPageDTO> findBooksByExample(
            @Parameter(description="Which page to return based on pageSize.")
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @Parameter(description="The maximum number of elements to provide in single page.")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Parameter(description="An ordered list of property/direction sets expressed as a string. " +
                    "Example: released:DESC,id:ASC")
            @RequestParam(value = "sort", required = false) String sortString,
            @Parameter(description = "Book properties to perform an exact match. ID property is ignored.")
            @RequestBody BookDTO probe) {

        Pageable pageable = PageableDTO.of(pageNumber, pageSize, sortString).toPageable();

        Page<BookDTO> result=booksService.findBooksMatchingAll(probe, pageable);

        BooksPageDTO resultDTO = new BooksPageDTO(result);
        ResponseEntity<BooksPageDTO> response = ResponseEntity.ok(resultDTO);
        return response;
    }
}
