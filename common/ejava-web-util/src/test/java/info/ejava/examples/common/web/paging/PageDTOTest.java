package info.ejava.examples.common.web.paging;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import info.ejava.examples.common.dto.DtoUtil;
import info.ejava.examples.common.dto.JsonUtil;
import info.ejava.examples.common.dto.XmlUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;

import javax.xml.bind.annotation.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.BDDAssertions.then;

@Slf4j
public class PageDTOTest {
    private static final List<Integer> SOME_INTS = Arrays.asList(1,2,3);
    private static final Sort aSort=Sort.by("title", "artist");
    private static List<MediaType> MEDIA_TYPES = Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML);
    private JsonUtil jsonUtil = JsonUtil.instance();
    private XmlUtil xmlUtil = XmlUtil.instance();

    @JacksonXmlRootElement(localName = "intPage", namespace = "urn:ejava.common.dto.test")
    @XmlRootElement(name = "intPage", namespace = "urn:ejava.common.dto.test")
    @XmlAccessorType(XmlAccessType.NONE)
    @NoArgsConstructor
    public static class IntegerPageDTO extends PageDTO<Integer> {
        @JsonProperty
        @JacksonXmlElementWrapper(localName = "content",  namespace="urn:ejava.common.dto.test")
        @XmlElementWrapper(name="content",  namespace= "urn:ejava.common.dto.test")
        @JacksonXmlProperty(localName = "value")
        @XmlElement(name="value")
        public List<Integer> getContent() {
            return super.getContent();
        }

        public IntegerPageDTO(List<Integer> content, Long totalElements, PageableDTO pageSpec) {
            super(content, totalElements, pageSpec);
        }
    }

    private static Stream<Arguments> responses() {
        return Stream.of(
                Arguments.of(new PageImpl(SOME_INTS, PageRequest.of(1,25, aSort), 100)),
                Arguments.of(new PageImpl(SOME_INTS, Pageable.unpaged(), 100)),
                Arguments.of(new PageImpl(SOME_INTS, Pageable.unpaged(), 0)),
                Arguments.of(new PageImpl(SOME_INTS)),
                Arguments.of(new PageImpl(SOME_INTS, PageRequest.of(1,25), 100)),
                Arguments.of(new PageImpl(Collections.emptyList(), PageRequest.of(22, 25), 100)),
                Arguments.of(new PageImpl(Collections.emptyList()))
        );
    }
    private static Stream<Arguments> marshal_unmarshal() {
        return responses().flatMap(resp->MEDIA_TYPES.stream().map(mt->Arguments.of(mt, resp.get()[0])));
    }
    private static Stream<Arguments> marshal_unmarshal_json() {
        return responses();
    }
    private static Stream<Arguments> marshal_unmarshal_xml() {
        return responses();
    }



    @ParameterizedTest
    @MethodSource
    void marshal_unmarshal(MediaType mediaType, Page<Integer> page) throws IOException {
        DtoUtil dtoUtil = MediaType.APPLICATION_XML==mediaType ? xmlUtil : jsonUtil;
        //given
        IntegerPageDTO response = PageDTO.fromPage(page, IntegerPageDTO::new);
        //when
        String string = dtoUtil.marshalThrows(response);
        log.info("{}", string);
        IntegerPageDTO pageResult = dtoUtil.unmarshalThrows(string, IntegerPageDTO.class);
        //then
        List<Integer> pageContent = pageResult.getContent();
        then(pageContent).isEqualTo(page.getContent());
        then(pageResult.getPageSize()).isEqualTo(page.getSize());
        then(pageResult.getPageNumber()).isEqualTo(page.getNumber());
        then(pageResult.getNumberOfElements()).isEqualTo(page.getNumberOfElements());
        then(pageResult.hasContent()).isEqualTo(page.hasContent());
        if (page.getTotalElements()!=0) {
            then(pageResult.getTotalElements()).isEqualTo(page.getTotalElements());
            then(pageResult.getTotalElementsOptional().get()).isEqualTo(page.getTotalElements());
        } else {
            then(pageResult.getTotalElements()).isNull();
            then(pageResult.getTotalElementsOptional()).isNotPresent();
        }
        then(pageResult.toPage()).isEqualTo(page);
    }

    @Test
    void next() {
        //given
        Pageable pageable = PageRequest.of(0, 25, aSort);
        Page<Integer> page = new PageImpl<Integer>(SOME_INTS, pageable, 0);
        IntegerPageDTO pageResponse = PageDTO.fromPage(page, IntegerPageDTO::new);
        PageableDTO pageSpec = pageResponse.next();
        pageable = pageable.next();

        for (int i=0; i<5; i++) {
            //given
            pageable = pageable.next();
            //when
            pageSpec = pageSpec.next();
            //then
            log.info("{}", pageable);
            log.info("{}", pageSpec);
            then(pageSpec.getPageNumber()).isEqualTo(pageable.getPageNumber());
            then(pageSpec.toPageable()).isEqualTo(pageable);
        }
    }

    @Test
    void previous() {
        //given
        Pageable pageable = PageRequest.of(5, 25, aSort);
        Page<Integer> page = new PageImpl<Integer>(SOME_INTS, pageable, 0);
        PageDTO<Integer> pageResponse = PageDTO.fromPage(page, IntegerPageDTO::new);
        PageableDTO pageSpec = pageResponse.previous();
        pageable = pageable.previousOrFirst();

        do {
            //given
            pageable = pageable.previousOrFirst();
            //when
            pageSpec = pageSpec.previous();
            //then
            log.info("{}", pageable);
            log.info("{}", pageSpec);
            then(pageSpec.getPageNumber()).isEqualTo(pageable.getPageNumber());
            then(pageSpec.hasPrevious()).isEqualTo(pageable.hasPrevious());
            then(pageSpec.toPageable()).isEqualTo(pageable);
        } while (pageable.hasPrevious());
    }
}
