package info.ejava.examples.common.web.paging;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.IOException;
import java.util.Map;

/**
 * This class is responsible for serializing and deserializing a Spring Data Pageable
 * object to/from an API. It has been mapped using Jackson JSON and XML, with a little
 * JAXB to get it to work with WebFlux. It is designed to be an immutable object -- thus
 * the mapping challenge is to have all properties known prior to construction.
 */
@EqualsAndHashCode
@Getter
@JsonDeserialize(builder= PageableDTO.DtoFacade.class)
@JsonSerialize(using= PageableDTO.Serializer.class)
@XmlAccessorType(XmlAccessType.NONE)
@XmlJavaTypeAdapter(value=PageableDTO.PageableAdapter.class)
public class PageableDTO {
    //properties used in queryParams
    public static final String PAGE_NUMBER="pageNumber";
    public static final String PAGE_SIZE="pageSize";
    public static final String SORT="sort";

    private final Integer pageNumber;
    private final Integer pageSize;
    private final String sortString;

    public PageableDTO(Integer pageNumber, Integer pageSize, String sort) {
        this.pageNumber = pageNumber!=null && pageNumber>=0 ? pageNumber : null;
        this.pageSize = pageSize!=null && pageSize>0 ? pageSize : null;
        this.sortString = sort;
    }

    public static PageableDTO unpaged() {
        return new PageableDTO(null, null, null);
    }
    public static PageableDTO of(Integer pageNumber, Integer pageSize) {
        return new PageableDTO(pageNumber, pageSize, null);
    }
    public static PageableDTO of(Integer pageNumber, Integer pageSize, Sort sort) {
        return new PageableDTO(pageNumber, pageSize, new SortEncoder(sort).getQueryString());
    }
    public static PageableDTO of(Integer pageNumber, Integer pageSize, String sortString) {
        return new PageableDTO(pageNumber, pageSize, sortString);
    }
    public static PageableDTO of(Pageable pageable) {
        if (pageable.isUnpaged()) {
            return unpaged();
        }
        return new PageableDTO(pageable.getPageNumber(), pageable.getPageSize(),
                new SortEncoder(pageable.getSort()).getQueryString());
    }

    public PageableDTO next() {
        return isPaged() ? PageableDTO.of(pageNumber +1, pageSize,sortString) : unpaged();
    }
    public PageableDTO previous() {
        if (isUnpaged()) {
            return unpaged();
        } else {
            int newPage = pageNumber - 1;
            return PageableDTO.of(newPage > 0 ? newPage : 0, pageSize, sortString);
        }
    }


    public boolean isUnpaged() {
        return pageNumber==null || pageSize==null;
    }
    public boolean isPaged() {
        return pageNumber!=null && pageSize!=null;
    }
    public boolean isSorted() {
        return SortEncoder.valueOf(sortString).isSorted();
    }
    public boolean hasPrevious() {
        return isPaged() && pageNumber-1 >= 0;
    }


    public UriBuilder addQueryParams(UriBuilder uriBuilder) {
        for (Map.Entry<String, String> entry:getQueryParams().toSingleValueMap().entrySet()) {
            uriBuilder.queryParam(entry.getKey(), entry.getValue());
        }
        return uriBuilder;
    }

    public MultiValueMap<String, String> getQueryParams() {
        MultiValueMap<String, String> queryParams = new HttpHeaders();
        if (isPaged()) {
            queryParams.add(PAGE_NUMBER, pageNumber.toString());
            queryParams.add(PAGE_SIZE, pageSize.toString());
            SortEncoder sort = SortEncoder.valueOf(sortString);
            if (sort.isSorted()) {
                queryParams.add(SORT, sort.getQueryString());
            }
        }
        return queryParams;
    }



    public static PageableDTO fromPageable(Pageable pageable) {
        return pageable!=null && pageable.isPaged() ?
                PageableDTO.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()) :
                PageableDTO.unpaged();
    }
    public Pageable toPageable() {
        if (pageSize==null || pageNumber==null) {
            return Pageable.unpaged();
        } else {
            return PageRequest.of(pageNumber, pageSize > 0 ? pageSize : Integer.MAX_VALUE,
                    SortEncoder.valueOf(sortString).getSort());
        }
    }
    public Sort toSort() {
        return SortEncoder.valueOf(sortString).getSort();
    }



    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();

        if (isUnpaged()) {
            text.append("pageable=UNPAGED");
        } else {
            text.append("pageNumber=").append(pageNumber);
            text.append(", pageSize=").append(pageSize);
        }
        if (sortString !=null) {
            text.append(", sort=").append(sortString);
        }
        return text.toString();
    }

    public DtoFacade toDtoFacade() {
        return new DtoFacade(pageNumber, pageSize, sortString);
    }

    /**
     * This mutable adapter class is used to unmarshal JSON/XML using Jackson and build
     * a resulting immutable object.
     */
    @JacksonXmlRootElement(localName = "pageable", namespace = "urn:ejava.common.dto")
    @XmlRootElement(name="pageable", namespace = "urn:ejava.common.dto")
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonPOJOBuilder
    public static class DtoFacade {
        @JsonProperty
        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        Integer pageNumber;
        @JsonProperty
        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        Integer pageSize;
        @JsonProperty
        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        String sort;
        public PageableDTO build() {
            return new PageableDTO(pageNumber, pageSize, sort);
        }
    }

    /**
     * This adapter is called by Jackson to marshal the unmutable PageableDTO. It does so by
     * copying the details to the mutable DtoFacade that is used to demarshal pageable contents.
     */
    public static class Serializer extends JsonSerializer<PageableDTO> {
        @Override
        public void serialize(PageableDTO value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            DtoFacade facade = new DtoFacade(value.getPageNumber(), value.getPageSize(), value.getSortString());
            gen.writeObject(facade);
        }
    }

    /**
     * This adapter is called by JAXB when a class -- like the PageableDTO -- declares
     * a XmlJavaTypeAdapter to handle marshaling and demarshaling through a facade object.
     * In this case the public class is immutable and JAXB wants to work with a mutable
     * instance. This approach works seamlessly if not used as root object.
     */
    static class PageableAdapter extends XmlAdapter<DtoFacade, PageableDTO> {
        @Override
        public PageableDTO unmarshal(DtoFacade xml) throws Exception {
            return new PageableDTO(xml.pageNumber, xml.pageSize, xml.sort);
        }
        @Override
        public DtoFacade marshal(PageableDTO dto) throws Exception {
            return new DtoFacade(dto.getPageNumber(), dto.getPageSize(), dto.getSortString());
        }
    }
}
