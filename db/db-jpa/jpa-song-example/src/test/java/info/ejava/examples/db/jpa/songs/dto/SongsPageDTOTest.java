package info.ejava.examples.db.jpa.songs.dto;

import info.ejava.examples.common.dto.DtoUtil;
import info.ejava.examples.common.dto.JsonUtil;
import info.ejava.examples.common.dto.XmlUtil;
import info.ejava.examples.common.web.paging.PageDTO;
import lombok.extern.slf4j.Slf4j;
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

import static info.ejava.examples.db.jpa.songs.dto.SongDTOFactory.*;
import static org.assertj.core.api.BDDAssertions.then;

@Slf4j
public class SongsPageDTOTest {
    private static final Sort aSort=Sort.by("title", "artist");
    private static List<MediaType> MEDIA_TYPES = Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML);
    private JsonUtil jsonUtil = JsonUtil.instance();
    private XmlUtil xmlUtil = XmlUtil.instance();
    private SongDTOFactory songDTOFactory = new SongDTOFactory();

    private static Stream<Arguments> pages() {
        List<SongDTO> songs = new SongDTOFactory().listBuilder()
                .songs(3, 3, oneUpId);
        return Stream.of(
                Arguments.of(new PageImpl(songs, PageRequest.of(1,25, aSort), 100)),
                Arguments.of(new PageImpl(songs, Pageable.unpaged(), 100)),
                Arguments.of(new PageImpl(songs, Pageable.unpaged(), 0)),
                Arguments.of(new PageImpl(songs)),
                Arguments.of(new PageImpl(songs, PageRequest.of(1,25), 100)),
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
        SongDTO originalDTO = songDTOFactory.make(oneUpId);
        //when
        String string = dtoUtil.marshalThrows(originalDTO);
        log.info("{}", string);
        SongDTO resultDTO = dtoUtil.unmarshalThrows(string, SongDTO.class);
        //then
        then(resultDTO).isEqualTo(originalDTO);
    }

    @Test
    void marshal_jaxb_unmarshal_jackson_song() throws JAXBException, IOException {
        //given
        SongDTO originalDTO = songDTOFactory.make(oneUpId);
        //when
        String xml = marshalJaxb(originalDTO);
        log.info("{}", xml);
        SongDTO resultDTO = xmlUtil.unmarshalThrows(xml, SongDTO.class);
        //then
        then(resultDTO).isEqualTo(originalDTO);
    }

    @ParameterizedTest
    @MethodSource
    void marshal_unmarshal(MediaType mediaType, Page<SongDTO> page) throws IOException {
        DtoUtil dtoUtil = MediaType.APPLICATION_XML==mediaType ? xmlUtil : jsonUtil;
        //given
        SongsPageDTO pageDTO = PageDTO.fromPage(page, SongsPageDTO::new);
        //when
        String string = dtoUtil.marshalThrows(pageDTO);
        log.info("{}", string);
        SongsPageDTO pageResult = dtoUtil.unmarshalThrows(string, SongsPageDTO.class);
        //then
        List<SongDTO> pageContent = pageResult.getContent();
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
    }


    @ParameterizedTest
    @MethodSource
    void page_marshal_jackson_unmarshal_jaxb(Page originalPage) throws JAXBException {
        //given
        SongsPageDTO pageDTO = new SongsPageDTO(originalPage);
        //when
//        String xml = marshalJaxb(pageDTO.toDtoFacade());
//        log.info("{}", xml);
        String xml = XmlUtil.instance().marshal(pageDTO);
        log.info("{}", xml);
        SongsPageDTO pageResult = unmarshalJaxb(xml, SongsPageDTO.class);

        //then
        log.info("{}", pageResult);
        List<SongDTO> resultContent = pageResult.getContent();
        then(resultContent).describedAs("content").isEqualTo(pageDTO.getContent());
        then(pageResult.getPageSize()).describedAs("pageSize").isEqualTo(originalPage.getSize());
        then(pageResult.getPageNumber()).describedAs("pageNumber").isEqualTo(originalPage.getNumber());
        then(pageResult.getNumberOfElements()).describedAs("numberOfElements").isEqualTo(originalPage.getNumberOfElements());
        then(pageResult.hasContent()).describedAs("hasContent").isEqualTo(originalPage.hasContent());
        then(pageResult.getTotalElements()).describedAs("totalElements").isEqualTo(originalPage.getTotalElements());
        then(pageResult.getTotalElementsOptional().get()).describedAs("totalElementsOptional").isEqualTo(originalPage.getTotalElements());
    }
}
