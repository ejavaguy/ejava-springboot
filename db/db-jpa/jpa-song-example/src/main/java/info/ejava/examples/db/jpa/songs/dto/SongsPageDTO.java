package info.ejava.examples.db.jpa.songs.dto;

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

@JacksonXmlRootElement(localName = "songsPage", namespace = "urn:ejava.db-repo.songs")
@XmlRootElement(name = "songsPage", namespace = "urn:ejava.db-repo.songs")
@XmlType(name = "SongsPage", namespace = "urn:ejava.db-repo.songs")
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class SongsPageDTO extends PageDTO<SongDTO> {
    @JsonProperty
    @JacksonXmlElementWrapper(localName = "content", namespace = "urn:ejava.common.dto")
    @JacksonXmlProperty(localName = "song", namespace = "urn:ejava.db-repo.songs")
    @XmlElementWrapper(name="content", namespace = "urn:ejava.common.dto")
    @XmlElement(name="song", namespace = "urn:ejava.db-repo.songs")
    public List<SongDTO> getContent() {
        return super.getContent();
    }

    public SongsPageDTO(List<SongDTO> content, Long totalElements, PageableDTO pageableDTO) {
        super(content, totalElements, pageableDTO);
    }

    public SongsPageDTO(Page<SongDTO> page) {
        this(page.getContent(), page.getTotalElements(), PageableDTO.fromPageable(page.getPageable()));
    }
}
