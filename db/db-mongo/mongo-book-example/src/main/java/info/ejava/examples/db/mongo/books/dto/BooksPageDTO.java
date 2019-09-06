package info.ejava.examples.db.mongo.books.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import info.ejava.examples.common.web.paging.PageDTO;
import info.ejava.examples.common.web.paging.PageableDTO;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import javax.xml.bind.annotation.*;
import java.util.List;

@JacksonXmlRootElement(localName = "booksPage", namespace = "urn:ejava.db-repo.books")
@XmlRootElement(name = "booksPage", namespace = "urn:ejava.db-repo.books")
@XmlType(name = "BooksPage", namespace = "urn:ejava.db-repo.books")
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class BooksPageDTO extends PageDTO<BookDTO> {
    @JsonProperty
    @JacksonXmlElementWrapper(localName = "content", namespace = "urn:ejava.common.dto")
    @JacksonXmlProperty(localName = "book", namespace = "urn:ejava.db-repo.books")
    @XmlElementWrapper(name="content", namespace = "urn:ejava.common.dto")
    @XmlElement(name="book", namespace = "urn:ejava.db-repo.books")
    public List<BookDTO> getContent() {
        return super.getContent();
    }

    public BooksPageDTO(List<BookDTO> content, Long totalElements, PageableDTO pageableDTO) {
        super(content, totalElements, pageableDTO);
    }

    public BooksPageDTO(Page<BookDTO> page) {
        this(page.getContent(), page.getTotalElements(), PageableDTO.fromPageable(page.getPageable()));
    }
}
