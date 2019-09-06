package info.ejava.examples.db.validation.contacts.dto;

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

import static info.ejava.examples.db.validation.contacts.dto.ValidationNamespace.VALIDATION_NAMESPACE;

@JacksonXmlRootElement(localName = "pocsPage", namespace = VALIDATION_NAMESPACE)
@XmlRootElement(name = "pocsPage", namespace = VALIDATION_NAMESPACE)
@XmlType(name = "PersonsPage", namespace = VALIDATION_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class PersonsPageDTO extends PageDTO<PersonPocDTO> {
    @JsonProperty
    @JacksonXmlElementWrapper(localName = "content", namespace = "urn:ejava.common.dto")
    @JacksonXmlProperty(localName = "person", namespace = VALIDATION_NAMESPACE)
    @XmlElementWrapper(name="content", namespace = "urn:ejava.common.dto")
    @XmlElement(name="person", namespace = VALIDATION_NAMESPACE)
    public List<PersonPocDTO> getContent() {
        return super.getContent();
    }

    public PersonsPageDTO(List<PersonPocDTO> content, Long totalElements, PageableDTO pageableDTO) {
        super(content, totalElements, pageableDTO);
    }

    public PersonsPageDTO(Page<PersonPocDTO> page) {
        this(page.getContent(), page.getTotalElements(), PageableDTO.fromPageable(page.getPageable()));
    }
}
