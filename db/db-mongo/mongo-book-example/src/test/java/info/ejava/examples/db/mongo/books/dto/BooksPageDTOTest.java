package info.ejava.examples.db.mongo.books.dto;

import info.ejava.examples.common.dto.DtoUtil;
import info.ejava.examples.common.dto.JsonUtil;
import info.ejava.examples.common.dto.XmlUtil;
import info.ejava.examples.common.web.paging.PageDTO;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.BDDAssertions.then;

@Slf4j
public class BooksPageDTOTest {
    private static final Sort aSort=Sort.by("title", "author");
    private static List<MediaType> MEDIA_TYPES = Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML);
    private JsonUtil jsonUtil = JsonUtil.instance();
    private XmlUtil xmlUtil = XmlUtil.instance();
    private BookDTOFactory bookDTOFactory = new BookDTOFactory();

    private static Stream<Arguments> pages() {
        List<BookDTO> books = new BookDTOFactory().listBuilder()
                .books(3, 3, BookDTOFactory.oneUpId);
        return Stream.of(
                Arguments.of(new PageImpl(books, PageRequest.of(1,25, aSort), 100)),
                Arguments.of(new PageImpl(books, Pageable.unpaged(), 100)),
                Arguments.of(new PageImpl(books, Pageable.unpaged(), 0)),
                Arguments.of(new PageImpl(books)),
                Arguments.of(new PageImpl(books, PageRequest.of(1,25), 100)),
                Arguments.of(new PageImpl(Collections.emptyList(), PageRequest.of(22, 25), 100)),
                Arguments.of(new PageImpl(Collections.emptyList()))
        );
    }
    private static Stream<Arguments> marshal_unmarshal() {
        return pages().flatMap(args->MEDIA_TYPES.stream().map(mt->Arguments.of(mt, args.get()[0])));
    }
    private static Stream<Arguments> page_marshal_jackson_unmarshal_jaxb() {
        return pages();
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

    @ParameterizedTest
    @ValueSource(strings = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    void marshal_unmarshal_song(String mediaType) throws IOException {
        //given
        DtoUtil dtoUtil = MediaType.APPLICATION_XML_VALUE.equals(mediaType) ? xmlUtil : jsonUtil;
        BookDTO originalDTO = bookDTOFactory.make(BookDTOFactory.oneUpId);
        //when
        String string = dtoUtil.marshalThrows(originalDTO);
        log.info("{}", string);
        BookDTO resultDTO = dtoUtil.unmarshalThrows(string, BookDTO.class);
        //then
        then(resultDTO).isEqualTo(originalDTO);
    }

    @Test
    void marshal_jaxb_unmarshal_jackson_song() throws JAXBException, IOException {
        //given
        BookDTO originalDTO = bookDTOFactory.make(BookDTOFactory.oneUpId);
        //when
        String xml = marshalJaxb(originalDTO);
        log.info("{}", xml);
        BookDTO resultDTO = xmlUtil.unmarshalThrows(xml, BookDTO.class);
        //then
        then(resultDTO).isEqualTo(originalDTO);
    }

    @ParameterizedTest
    @MethodSource
    void marshal_unmarshal(MediaType mediaType, Page<BookDTO> page) throws IOException {
        DtoUtil dtoUtil = MediaType.APPLICATION_XML==mediaType ? xmlUtil : jsonUtil;
        //given
        BooksPageDTO pageDTO = PageDTO.fromPage(page, BooksPageDTO::new);
        //when
        String string = dtoUtil.marshalThrows(pageDTO);
        log.info("{}", string);
        BooksPageDTO pageResult = dtoUtil.unmarshalThrows(string, BooksPageDTO.class);
        //then
        List<BookDTO> pageContent = pageResult.getContent();
        BDDAssertions.then(pageContent).isEqualTo(page.getContent());
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
    }


    @ParameterizedTest
    @MethodSource
    void page_marshal_jackson_unmarshal_jaxb(Page originalPage) throws JAXBException {
        //given
        BooksPageDTO pageDTO = new BooksPageDTO(originalPage);
        //when
//        String xml = marshalJaxb(pageDTO.toDtoFacade());
//        log.info("{}", xml);
        String xml = XmlUtil.instance().marshal(pageDTO);
        log.info("{}", xml);
        BooksPageDTO pageResult = unmarshalJaxb(xml, BooksPageDTO.class);

        //then
        log.info("{}", pageResult);
        List<BookDTO> resultContent = pageResult.getContent();
        BDDAssertions.then(resultContent).describedAs("content").isEqualTo(pageDTO.getContent());
        then(pageResult.getPageSize()).describedAs("pageSize").isEqualTo(originalPage.getSize());
        then(pageResult.getPageNumber()).describedAs("pageNumber").isEqualTo(originalPage.getNumber());
        then(pageResult.getNumberOfElements()).describedAs("numberOfElements").isEqualTo(originalPage.getNumberOfElements());
        then(pageResult.hasContent()).describedAs("hasContent").isEqualTo(originalPage.hasContent());
        then(pageResult.getTotalElements()).describedAs("totalElements").isEqualTo(originalPage.getTotalElements());
        then(pageResult.getTotalElementsOptional().get()).describedAs("totalElementsOptional").isEqualTo(originalPage.getTotalElements());
    }
}
