package info.ejava.examples.common.web.paging;

import info.ejava.examples.common.dto.DtoUtil;
import info.ejava.examples.common.dto.JsonUtil;
import info.ejava.examples.common.dto.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.data.domain.Sort.by;

@Slf4j
public class PageableDTOTest {
    private static final List<MediaType> mediaTypes = Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML);
    private JsonUtil jsonUtil = JsonUtil.instance();
    private XmlUtil xmlUtil = XmlUtil.instance();
    private static final Sort SORT = by(Sort.Direction.ASC, "title", "artist")
            .and(by(Sort.Direction.DESC, "release"))
            .and(by(Sort.Direction.ASC, "id"));

    private static Stream<Arguments> page_requests() {
        return Stream.of(
                Arguments.of(Pageable.unpaged()),
                Arguments.of(PageRequest.of(0, 25)),
                Arguments.of(PageRequest.of(0, 25, SORT)),
                Arguments.of(PageRequest.of(10, 25), null));
    }
    private static Stream<Arguments> mediaType_page_requests() {
        return page_requests().flatMap(a->mediaTypes.stream().map(mt->Arguments.of(mt,a.get()[0])));
    }

    private static Stream<Arguments> marshal_unmarshal() {
        return mediaType_page_requests();
    }
    private static Stream<Arguments> marshal_unmarshal_query_param() {
        return page_requests();
    }

    @ParameterizedTest
    @MethodSource
    void marshal_unmarshal(MediaType mediaType, Pageable originalPageable) throws IOException {
        DtoUtil dtoUtil = MediaType.APPLICATION_XML==mediaType ? xmlUtil : jsonUtil;

        //given
        PageableDTO page = PageableDTO.of(originalPageable);

        //when
        String string = dtoUtil.marshalThrows(page);
        log.info("{}", string);
        PageableDTO pageResult = dtoUtil.unmarshalThrows(string, PageableDTO.class);

        //then
        Pageable pageableResult = pageResult.toPageable();
        then(pageableResult).isEqualTo(originalPageable);
        if (originalPageable.isPaged()) {
            then(pageResult.getPageNumber()).isEqualTo(originalPageable.getPageNumber());
            then(pageResult.getPageSize()).isEqualTo(originalPageable.getPageSize());
            Sort sortResult = pageResult.toSort();
            then(sortResult).isEqualTo(originalPageable.getSort());
        } else {
            Sort sortResult = pageResult.toSort();
            then(sortResult).isEqualTo(Sort.unsorted());
        }
    }


    @ParameterizedTest
    @MethodSource()
    void marshal_unmarshal_query_param(Pageable originalPageable) {
        //given
        UriBuilder ub = UriComponentsBuilder.newInstance();
        PageableDTO page = PageableDTO.of(originalPageable);
        //when
        URI uri = page.addQueryParams(ub).build();
        log.info("{}", uri);
        //then
        Integer pageNumber=getQueryParam(uri, PageableDTO.PAGE_NUMBER, Integer.class);
        Integer pageSize=getQueryParam(uri, PageableDTO.PAGE_SIZE, Integer.class);
        String sort=getQueryParam(uri, PageableDTO.SORT, String.class);
        if (originalPageable.isUnpaged()) {
            then(pageNumber).isNull();
            then(pageSize).isNull();
            then(sort).isNull();
        } else {
            then(pageNumber).isEqualTo(originalPageable.getPageNumber());
            then(pageSize).isEqualTo(originalPageable.getPageSize());
            Sort sortResult = SortEncoder.valueOf(sort).getSort();
            then(sortResult).isEqualTo(originalPageable.getSort());
        }
    }

    private <T> T getQueryParam(URI uri, String name, Class<T> type) {
        if (uri.getQuery()==null) {
            return null;
        }
        String value = Arrays.stream(uri.getQuery().split("&"))
                .filter(q->q.startsWith(name))
                .map(q->q.substring(name.length()+1))
                .findFirst()
                .orElse(null);
        if (value==null) {
            return null;
        } else if (type==String.class) {
            return (T)value;
        } else {
            return (T)Integer.valueOf(value);
        }
    }


    private <T> T unmarshalJaxb(String xml, Class<T> type) throws JAXBException {
        JAXBContext jbx = JAXBContext.newInstance(type);
        Unmarshaller u = jbx.createUnmarshaller();
        ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        return (T) u.unmarshal(bis);
    }
    private <T> String marshalJaxb(Object object) throws JAXBException {
        JAXBContext jbx = JAXBContext.newInstance(object.getClass());
        Marshaller m = jbx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        m.marshal(object, bos);
        return bos.toString();
    }

    @Test
    void pageable_marshal_jackson_unmarshal_jaxb() throws JAXBException, IOException {
        //given
        Pageable originalPageable = PageRequest.of(3, 5, Sort.by("released"));
        PageableDTO pageableDTO = PageableDTO.fromPageable(originalPageable);

        //when
        String xml = marshalJaxb(pageableDTO.toDtoFacade());
        log.info("{}", xml);
        xml = XmlUtil.instance().marshalThrows(pageableDTO);
        log.info("{}", xml);
        PageableDTO pageableResult = unmarshalJaxb(xml, PageableDTO.DtoFacade.class).build();
        //then
        log.info("{}", pageableResult);
        then(pageableResult.getPageNumber()).isEqualTo(originalPageable.getPageNumber());
        then(pageableResult.getPageSize()).isEqualTo(pageableResult.getPageSize());
        then(pageableResult.toSort()).isEqualTo(originalPageable.getSort());
    }
}
