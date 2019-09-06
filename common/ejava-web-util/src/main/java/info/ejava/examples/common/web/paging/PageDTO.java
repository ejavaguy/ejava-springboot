package info.ejava.examples.common.web.paging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@JacksonXmlRootElement(localName = "page", namespace = "urn:ejava.common.dto")
@XmlRootElement(name = "page", namespace = "urn:ejava.common.dto")
@XmlType(name = "PageDTO", namespace = "urn:ejava.common.dto")
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class PageDTO<T> {
    private List<T> content;

    @JsonProperty
    @JacksonXmlProperty(isAttribute = true)
    @XmlAttribute
    private Long totalElements;
    @JsonIgnore
    private PageableDTO pageable;

    public PageDTO(List<T> content, Long totalElements, PageableDTO pageSpec) {
        this.content = content!=null ? content : new ArrayList<>(0);
        this.totalElements = totalElements;
        this.pageable = pageSpec!=null ? pageSpec : PageableDTO.unpaged();
    }

    public List<T> getContent() {
        if (content==null) {
            content=new ArrayList<>();
        }
        return content;
    }

    @JsonIgnore
    public int getPageNumber() {
        return ifPageableNotNull(p->p.getPageNumber(), 0);
    }
    @JsonIgnore
    public int getPageSize() {
        return ifPageableNotNull(p->p.getPageSize(), content.size());
    }
    @JsonIgnore
    public String getSort() {
        return ifPageableNotNull(p->p.getSortString(), null);
    }

    private <T> T ifPageableNotNull(Function<PageableDTO, T> getter, T defaultValue) {
        if (pageable==null) {
            return defaultValue;
        } else {
            T value = getter.apply(pageable);
            return value!=null ? value : defaultValue;
        }
    }

    @JsonProperty(value = "pageable")
    @JacksonXmlProperty(localName = "pageable", namespace = "urn:ejava.common.dto")
    @XmlElement(name="pageable", namespace = "urn:ejava.common.dto")
    PageableDTO getNullablePageSpec() {
        return pageable!=null && pageable.isPaged() ? pageable : null;
    }
    void setNullablePageSpec(PageableDTO pageable) {
        this.pageable = pageable!=null ? pageable : PageableDTO.unpaged();
    }

    @JsonIgnore
    public PageableDTO getPageableDTO() {
        return pageable;
    }

    @JsonIgnore
    public Optional<Long> getTotalElementsOptional() {
        return totalElements!=null ? Optional.of(totalElements) : Optional.empty();
    }
    @JsonIgnore
    public int getNumberOfElements() {
        return content.size();
    }
    public boolean hasNextPage() {
        return !content.isEmpty();
    }
    public boolean hasContent() {
        return !content.isEmpty();
    }

    public PageableDTO next() {
        return pageable.next();
    }
    public PageableDTO previous() {
        return pageable.previous();
    }

    public interface Ctor<T,R extends PageDTO> {
        public R apply(List<T> content, Long totalElements, PageableDTO pageable);
    }

    public static <T,R extends PageDTO<T>> R fromPage(Page<T> page, Ctor<T,R> ctor) {
        return ctor.apply(
                page.getContent(),
                page.getTotalElements()!=0 ? page.getTotalElements() : null,
                PageableDTO.fromPageable(page.getPageable()));
    }
    public Page<T> toPage() {
        return new PageImpl<T>(content,
                pageable!=null ? pageable.toPageable() : Pageable.unpaged(),
                totalElements!=null ? totalElements.intValue() : 0);
    }

    @Override
    public String toString() {
        String contentType = content.stream()
                .findFirst()
                .map(c->c.getClass().getSimpleName())
                .orElse("UNKNOWN");
        StringBuilder text = new StringBuilder();
        text.append("PagedResponse{numberOfElements=").append(getNumberOfElements());
        if (pageable !=null) {
            text.append(", page=").append(pageable);
        }
        if (totalElements != null) {
            text.append(", totalElements=").append(totalElements);
        }
        text.append(", contentType=").append(contentType)
                .append("'}'");
        return text.toString();
    }
}
