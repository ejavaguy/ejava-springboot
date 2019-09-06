package info.ejava.examples.db.mongo.books.svc;

import info.ejava.examples.db.mongo.books.dto.BookDTO;
import info.ejava.examples.db.mongo.books.bo.Book;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BooksMapper {
    public Date map(LocalDate ld) {
        Date dt=null;
        if (ld!=null) {
            Instant instant = ld.atStartOfDay().toInstant(ZoneOffset.UTC);
            dt=Date.from(instant);
        }
        return dt;
    }

    public LocalDate map(Date dt) {
        LocalDate ld = null;
        if (dt!=null) {
            Instant instant = dt.toInstant();
            ld = LocalDate.ofInstant(instant, ZoneOffset.UTC);
        }
        return ld;
    }

    public Book map(BookDTO dto) {
        Book bo = null;
        if (dto!=null) {
            bo = Book.builder()
                    .id(dto.getId())
                    .author(dto.getAuthor())
                    .title(dto.getTitle())
                    .published(dto.getPublished())
                    .build();
        }
        return bo;
    }

    public BookDTO map(Book bo) {
        BookDTO dto = null;
        if (bo!=null) {
            dto = BookDTO.builder()
                    .id(bo.getId())
                    .author(bo.getAuthor())
                    .title(bo.getTitle())
                    .published(bo.getPublished())
                    .build();
        }
        return dto;
    }

    public List<Book> map(Collection<BookDTO> dtos) {
        List<Book> bos = null;
        if (dtos!=null) {
            bos = dtos.stream().map(dto->map(dto)).collect(Collectors.toList());
        }
        return bos;
    }

    public Page<BookDTO> map(Page<Book> bos) {
        Page<BookDTO> dtos = null;
        if (bos!=null) {
            dtos = bos.map(bo->map(bo));
        }
        return dtos;
    }
}
