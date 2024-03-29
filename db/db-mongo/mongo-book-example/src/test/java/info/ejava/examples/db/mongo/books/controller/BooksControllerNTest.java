package info.ejava.examples.db.mongo.books.controller;

import info.ejava.examples.common.dto.DtoUtil;
import info.ejava.examples.common.dto.JsonUtil;
import info.ejava.examples.common.dto.XmlUtil;
import info.ejava.examples.common.web.ServerConfig;
import info.ejava.examples.common.web.paging.PageableDTO;
import info.ejava.examples.common.webflux.WebClientLoggingFilter;
import info.ejava.examples.db.mongo.books.TestProfileResolver;
import info.ejava.examples.db.mongo.books.dto.BookDTO;
import info.ejava.examples.db.mongo.books.NTestConfiguration;
import info.ejava.examples.db.mongo.books.dto.BookDTOFactory;
import info.ejava.examples.db.mongo.books.dto.BooksPageDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes = {NTestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
//properties = "spring.data.mongodb.uri=mongodb://admin:secret@localhost:27017/test?authSource=admin"
)
@ActiveProfiles(resolver = TestProfileResolver.class)
@Tag("springboot")
@Slf4j
public class BooksControllerNTest {
    @LocalServerPort
    private int port;
    private ServerConfig serverConfig;
    @Autowired
    private BookDTOFactory songDTOFactory;
    //@Autowired
    private WebTestClient wtc;
    @Autowired
    private WebClient webClient;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TestRestTemplate rtt;
    private DtoUtil jsonUtil=JsonUtil.instance();
    private DtoUtil xmlUtil=XmlUtil.instance();

    @BeforeEach
    void init() {
        serverConfig = new ServerConfig().withPort(port).build();
        log.info("{}", serverConfig.getBaseUrl());
        wtc = WebTestClient.bindToServer()
                .baseUrl(serverConfig.getBaseUrl().toString())
                .filter(WebClientLoggingFilter.requestFilter())
                .filter(WebClientLoggingFilter.responseFilter())
                .codecs(conf->{
            conf.defaultCodecs().jaxb2Encoder(new Jaxb2XmlEncoder());
            conf.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder());
        }).build();
    }

    @Test
    void create_song() {
        //given
        BookDTO song = songDTOFactory.make();
        WebTestClient.RequestHeadersSpec<?> request = wtc.post()
                .uri(BooksController.BOOKS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(song), BookDTO.class);
        //when
        WebTestClient.ResponseSpec response = request.exchange();

        //then
        response.expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
        BookDTO createdSong = response
                .returnResult(BookDTO.class)
                .getResponseBody().blockFirst();
        then(createdSong.getId()).isNotNull();

        String expectedLocation = UriComponentsBuilder
                .fromUri(serverConfig.getBaseUrl())
                .path(BooksController.BOOK_PATH)
                .build(createdSong.getId())
                .toString();
        response.expectHeader().location(expectedLocation);

        thenSongsEqual(createdSong, song);
    }

    @Test
    void get_song() {
        //given
        BookDTO existingSong = createSong();

        //when
        WebTestClient.ResponseSpec response = wtc.get()
                .uri(BooksController.BOOK_PATH, existingSong.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        //then
        BookDTO returnedSong = response.expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(BookDTO.class)
                .getResponseBody().blockFirst();

        thenSongsEqual(returnedSong, existingSong);
    }

    @Test
    void update_song() {
        //given
        BookDTO song = createSong();
        String originalTitle = song.getTitle();

        //when
        song.setTitle("modified");
        WebTestClient.ResponseSpec response = wtc.put()
                .uri(BooksController.BOOK_PATH, song.getId())
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(song), BookDTO.class)
                .exchange();
        //then
        response.expectStatus().isNoContent();
        BookDTO updatedSong = get_song(song.getId());

        then(updatedSong.getTitle()).isNotEqualTo(originalTitle);
        thenSongsEqual(updatedSong, song);
    }

    @Test
    void delete_song() {
        //given
        BookDTO song = createSong();
        //when
        WebTestClient.ResponseSpec response = wtc.delete()
                .uri(BooksController.BOOK_PATH, song.getId())
                .exchange();
        //then
        response.expectStatus().isNoContent();
        wtc.get()
                .uri(BooksController.BOOK_PATH, song.getId())
                .exchange()
                .expectStatus().isNotFound();
    }

    static boolean finderInitialized;
    private static Map<String, BookDTO> songs;

    @Nested
    class Finders {
        private UriComponentsBuilder findByExampleUriBuilder = null;

        @BeforeEach
        void populate() {
            findByExampleUriBuilder = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                    .path(BooksController.BOOKS_PATH).path("/example");
            if (!finderInitialized) {
                URI songsUri = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                        .path(BooksController.BOOKS_PATH)
                        .build().toUri();
                wtc.delete().uri(songsUri).exchange().expectStatus().isNoContent();
                songs = createSongs(10).stream().collect(Collectors.toMap(s -> s.getId(), s -> s));
                finderInitialized=true;
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
        void find_all(String mediaTypeValue) throws IOException {
            //given
            MediaType mediaType = MediaType.valueOf(mediaTypeValue);
            Map<String, BookDTO> allSongs = new HashMap(songs);
            BookDTO allSongsProbe = new BookDTO();
            //when
            WebTestClient.RequestHeadersSpec<?> request = wtc.post()
                    .uri(findByExampleUriBuilder.build().toUri())
                    .contentType(mediaType)
                    .body(Mono.just(allSongsProbe), BookDTO.class)
                    .accept(mediaType);
            //when
            log.info("{}", request.exchange().returnResult(String.class));
            BooksPageDTO pageDTO = request.exchange()
                    .expectStatus().isOk()
                    .returnResult(BooksPageDTO.class)
                    .getResponseBody().blockFirst();
            //then
            log.info("page: {}", pageDTO);
            then(pageDTO.getNumberOfElements()).isEqualTo(allSongs.size());
            then(pageDTO.getTotalElements()).isEqualTo(songs.size());
            then(pageDTO.getPageableDTO()).isEqualTo(PageableDTO.unpaged());
            then(pageDTO.getPageableDTO().toPageable()).isEqualTo(PageableDTO.unpaged().toPageable());

            Page<BookDTO> songPage = pageDTO.toPage();
            then(new HashSet<>(songPage.getContent())).isEqualTo(new HashSet<>(allSongs.values()));
            then(songPage.getTotalElements()).isEqualTo(allSongs.size());
            then(songPage.getPageable()).isEqualTo(pageDTO.getPageable().toPageable());

            for (BookDTO s: pageDTO.getContent()) {
                then(allSongs.remove(s.getId())).isNotNull();
            }
            then(allSongs).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
        void find_all_paged(String mediaTypeValue) {
            //given
            MediaType mediaType = MediaType.valueOf(mediaTypeValue);
            Map<String, BookDTO> allSongs = new HashMap(songs);
            BookDTO allSongsProbe = new BookDTO();
            PageableDTO pageSpec = PageableDTO.of(0, 3);
            int pageNumber=0;

            //verify
            BooksPageDTO page = null;
            while (page==null || page.getNumberOfElements()>0) {
                //given
                final PageableDTO targetPage = pageSpec;
                URI uri = findByExampleUriBuilder.cloneBuilder()
                        .queryParams(targetPage.getQueryParams()).build().toUri();
                WebTestClient.RequestHeadersSpec<?> request = wtc.post()
                        .uri(uri)
                        .contentType(mediaType)
                        .body(Mono.just(allSongsProbe), BookDTO.class)
                        .accept(mediaType);

                //when
                log.info("{}", request.exchange().returnResult(String.class));
                page = request.exchange()
                        .expectStatus().isOk()
                        .returnResult(BooksPageDTO.class)
                        .getResponseBody().blockFirst();

                //then
                log.info("page: {}", page);
                then(page.getPageSize()).describedAs("pageSize").isEqualTo(pageSpec.getPageSize());
                then(page.getPageNumber()).describedAs("pageNumber").isEqualTo(pageNumber);
                then(page.getNumberOfElements()).describedAs("numberOfElements").isLessThanOrEqualTo(targetPage.getPageSize());
                for (BookDTO s: page.getContent()) {
                    then(allSongs.remove(s.getId())).isNotNull();
                }
                then(page.getTotalElements()).describedAs("totalElements").isEqualTo(songs.size());

                pageSpec=pageSpec.next();
                pageNumber+=1;
            }
            then(allSongs).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
        void findall_sorted(String mediaTypeValue) {
            //given
            MediaType mediaType = MediaType.valueOf(mediaTypeValue);
            List<BookDTO> matchingSongs = songs.values().stream()
                    .filter(s -> s.getPublished()!=null)
                    .sorted((s1,s2)->s1.getPublished().compareTo(s2.getPublished()))
                    .collect(Collectors.toList());
            PageRequest pageable = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("published"), Sort.Order.asc("id")));
            PageableDTO pageSpec = PageableDTO.of(pageable);
            BookDTO allSongsProbe = BookDTO.builder().build();
            URI uri = findByExampleUriBuilder.queryParams(pageSpec.getQueryParams()).build().toUri();
            WebTestClient.RequestHeadersSpec<?> request = wtc.post()
                    .uri(uri)
                    .contentType(mediaType)
                    .body(Mono.just(allSongsProbe), BookDTO.class)
                    .accept(mediaType);

            //when
            log.info("{}", request.exchange().returnResult(String.class));
            BooksPageDTO pageDTO = request
                    .exchange()
                    .expectStatus().isOk()
                    .returnResult(BooksPageDTO.class)
                    .getResponseBody().blockFirst();
            //then
            then(pageDTO.getNumberOfElements()).isEqualTo(pageSpec.getPageSize());
            then(pageDTO.getTotalElements()).isEqualTo(songs.size());

            Page<BookDTO> songPage = pageDTO.toPage();
            then(songPage.getTotalElements()).isEqualTo(songs.size());
            then(songPage.getPageable()).isEqualTo(pageDTO.getPageable().toPageable());

            LocalDate previous = null;
            for (BookDTO song: pageDTO.getContent()) {
                if (previous!=null) {
                    then(previous).isAfterOrEqualTo(song.getPublished());
                }
                previous=song.getPublished();
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
        void findall_sorted_String(String mediaTypeValue) throws IOException {
            //given
            MediaType mediaType = MediaType.valueOf(mediaTypeValue);
            DtoUtil dtoUtil = MediaType.APPLICATION_XML.equals(mediaType) ? xmlUtil : jsonUtil;
            List<BookDTO> matchingSongs = songs.values().stream()
                    .filter(s -> s.getPublished()!=null)
                    .sorted((s1,s2)->s1.getPublished().compareTo(s2.getPublished()))
                    .collect(Collectors.toList());
            PageRequest pageable = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("published")));
            PageableDTO pageSpec = PageableDTO.of(pageable);
            BookDTO allSongsProbe = BookDTO.builder().build();
            log.info("probe={}", dtoUtil.marshal(allSongsProbe));
            URI uri = findByExampleUriBuilder.queryParams(pageSpec.getQueryParams()).build().toUri();
            WebTestClient.RequestHeadersSpec<?> request = wtc.post()
                    .uri(uri)
                    .contentType(mediaType)
                    .body(Mono.just(allSongsProbe), BookDTO.class)
                    .accept(mediaType);

            //when
            log.info("{}", request.exchange().returnResult(String.class));
            String pageString = request
                    .exchange()
                    .expectStatus().isOk()
                    .returnResult(String.class)
                    .getResponseBody().blockFirst();
            BooksPageDTO page = dtoUtil.unmarshalThrows(pageString, BooksPageDTO.class);
            //then
            log.info("{}", page);
            then(page.getNumberOfElements()).describedAs("numberOfElements").isEqualTo(pageSpec.getPageSize());
            then(page.getTotalElements()).describedAs("totalElements").isEqualTo(songs.size());
            LocalDate previous = null;
            for (BookDTO song: page.getContent()) {
                if (previous!=null) {
                    then(previous).isAfterOrEqualTo(song.getPublished());
                }
                previous=song.getPublished();
            }
        }


        @ParameterizedTest
        @ValueSource(strings = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
        void findall_sorted_restClient_string(String mediaTypeValue) throws IOException {
            //given
            MediaType mediaType = MediaType.valueOf(mediaTypeValue);
            DtoUtil dtoUtil = MediaType.APPLICATION_XML.equals(mediaType) ? xmlUtil : jsonUtil;
            List<BookDTO> matchingSongs = songs.values().stream()
                    .filter(s -> s.getPublished()!=null)
                    .sorted((s1,s2)->s1.getPublished().compareTo(s2.getPublished()))
                    .collect(Collectors.toList());
            PageRequest pageable = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("published")));
            PageableDTO pageSpec = PageableDTO.of(pageable);
            BookDTO allSongsProbe = BookDTO.builder().build();
            log.info("probe={}", dtoUtil.marshal(allSongsProbe));
            URI uri = findByExampleUriBuilder.queryParams(pageSpec.getQueryParams()).build().toUri();
            WebClient.RequestHeadersSpec<?> request = webClient.post()
                    .uri(uri)
                    .contentType(mediaType)
                    .body(Mono.just(allSongsProbe), BookDTO.class)
                    .accept(mediaType);

            //when
            ResponseEntity<String> response = request.retrieve().toEntity(String.class).block();

            //then
            then(response.getStatusCode().is2xxSuccessful()).isTrue();
            String pageString = response.getBody();
            log.info("{}", pageString);

            BooksPageDTO page = dtoUtil.unmarshal(pageString, BooksPageDTO.class);
            log.info("{}", page);
            then(page.getNumberOfElements()).isEqualTo(pageSpec.getPageSize());
            then(page.getTotalElements()).isEqualTo(songs.size());
            LocalDate previous = null;
            for (BookDTO song: page.getContent()) {
                if (previous!=null) {
                    then(previous).isAfterOrEqualTo(song.getPublished());
                }
                previous=song.getPublished();
            }
            then(page.getPageableDTO()).isNotNull();
            then(page.getPageSize()).isEqualTo(pageable.getPageSize());
            then(page.getPageNumber()).isEqualTo(pageable.getPageNumber());
            then(page.getSort()).isEqualTo("published:DESC");
        }


        @ParameterizedTest
        @ValueSource(strings = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
        void findall_sorted_restClient_type(String mediaTypeValue) throws IOException {
            //given
            MediaType mediaType = MediaType.valueOf(mediaTypeValue);
            DtoUtil dtoUtil = MediaType.APPLICATION_XML.equals(mediaType) ? xmlUtil : jsonUtil;
            List<BookDTO> matchingSongs = songs.values().stream()
                    .filter(s -> s.getPublished()!=null)
                    .sorted((s1,s2)->s1.getPublished().compareTo(s2.getPublished()))
                    .collect(Collectors.toList());
            PageRequest pageable = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("published")));
            PageableDTO pageSpec = PageableDTO.of(pageable);
            BookDTO allSongsProbe = BookDTO.builder().build();
            log.info("probe={}", dtoUtil.marshal(allSongsProbe));
            URI uri = findByExampleUriBuilder.queryParams(pageSpec.getQueryParams()).build().toUri();
            WebClient.RequestHeadersSpec<?> request = webClient.post()
                    .uri(uri)
                    .contentType(mediaType)
                    .body(Mono.just(allSongsProbe), BookDTO.class)
                    .accept(mediaType);

            //when
            ResponseEntity<BooksPageDTO> response = request.retrieve().toEntity(BooksPageDTO.class).block();

            //then
            then(response.getStatusCode().is2xxSuccessful()).isTrue();
            BooksPageDTO page = response.getBody();
            log.info("{}", page);
            then(page.getNumberOfElements()).isEqualTo(pageSpec.getPageSize());
            then(page.getTotalElements()).isEqualTo(songs.size());
            LocalDate previous = null;
            for (BookDTO song: page.getContent()) {
                if (previous!=null) {
                    then(previous).isAfterOrEqualTo(song.getPublished());
                }
                previous=song.getPublished();
            }
            then(page.getPageable()).isNotNull();
            then(page.getPageSize()).isEqualTo(pageable.getPageSize());
            then(page.getPageNumber()).isEqualTo(pageable.getPageNumber());
            then(page.getSort()).isEqualTo("published:DESC");
        }

        @ParameterizedTest
        @ValueSource(strings = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
        void findall_sorted_restTemplate(String mediaTypeValue) throws IOException {
            //given
            MediaType mediaType = MediaType.valueOf(mediaTypeValue);
            DtoUtil dtoUtil = MediaType.APPLICATION_XML.equals(mediaType) ? xmlUtil : jsonUtil;
            List<BookDTO> matchingSongs = songs.values().stream()
                    .filter(s -> s.getPublished()!=null)
                    .sorted((s1,s2)->s1.getPublished().compareTo(s2.getPublished()))
                    .collect(Collectors.toList());
            PageRequest pageable = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("published")));
            PageableDTO pageSpec = PageableDTO.of(pageable);
            BookDTO allSongsProbe = BookDTO.builder().build();
            log.info("probe={}", dtoUtil.marshal(allSongsProbe));
            URI uri = findByExampleUriBuilder
                    .queryParams(pageSpec.getQueryParams())
                    .build().toUri();
            RequestEntity request = RequestEntity.post(uri)
                    .accept(mediaType)
                    .contentType(mediaType)
                    .body(allSongsProbe);

            //when
            ResponseEntity<BooksPageDTO> response = restTemplate.exchange(request, BooksPageDTO.class);
            
            //then
            then(response.getStatusCode().is2xxSuccessful()).isTrue();
            BooksPageDTO page = response.getBody();
            log.info("{}", page);
            then(page.getNumberOfElements()).isEqualTo(pageSpec.getPageSize());
            then(page.getTotalElements()).isEqualTo(songs.size());
            LocalDate previous = null;
            for (BookDTO song: page.getContent()) {
                if (previous!=null) {
                    then(previous).isAfterOrEqualTo(song.getPublished());
                }
                previous=song.getPublished();
            }
            then(page.getPageableDTO()).isNotNull();
            then(page.getPageSize()).isEqualTo(pageable.getPageSize());
            then(page.getPageNumber()).isEqualTo(pageable.getPageNumber());
            then(page.getSort()).isEqualTo("published:DESC");
        }


        @Test
        void find_matching() {
            //given
            String artist = songs.values().stream().filter(s->s.getAuthor()!=null).findFirst().get().getAuthor();
            List<BookDTO> matchingSongs = songs.values().stream()
                    .filter(s -> artist.equals(s.getAuthor()))
                    .sorted((s1,s2)->s1.getId().compareTo(s2.getId()))
                    .collect(Collectors.toList());
            BookDTO allSongsProbe = BookDTO.builder().author(artist).build();
            PageableDTO pageSpec = PageableDTO.of(0, 3, Sort.by(Sort.Direction.ASC, "id"));
            URI uri = findByExampleUriBuilder.queryParams(pageSpec.getQueryParams()).build().toUri();
            WebTestClient.RequestHeadersSpec<?> request = wtc.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(allSongsProbe), BookDTO.class)
                    .accept(MediaType.APPLICATION_JSON);

            //when
            log.info("{}", request.exchange().returnResult(String.class));
            BooksPageDTO page = request
                    .exchange()
                    .expectStatus().isOk()
                    .returnResult(BooksPageDTO.class)
                    .getResponseBody().blockFirst();

            //then
            then(page.getContent()).isEqualTo(matchingSongs);
            then(page.getPageSize()).isEqualTo(pageSpec.getPageSize());
            then(page.getPageNumber()).isEqualTo(0);
            then(page.getPageableDTO().toPageable()).isEqualTo(pageSpec.toPageable());
            then(page.getPageableDTO().toPageable().getSort()).isEqualTo(pageSpec.toPageable().getSort());
        }


        @Test
        void find_random() {
            //given
            WebTestClient.RequestHeadersSpec<?> request = wtc.get()
                    .uri(BooksController.RANDOM_BOOK_PATH)
                    .accept(MediaType.APPLICATION_JSON);

            //when
            log.info("{}", request.exchange().returnResult(String.class));
            BookDTO randomBook = request
                    .exchange()
                    .expectStatus().isOk()
                    .returnResult(BookDTO.class)
                    .getResponseBody().blockFirst();
            log.info("randomBook={}", randomBook);

            //then
            then(randomBook).isNotNull();
        }
    }





    void thenSongsEqual(BookDTO result, BookDTO expected) {
        if (expected.getId()==null) { //we don't know what the value will be
            then(result.getId()).isNotNull(); //but it will be assigned
        } else {
            then(result.getId()).isEqualTo(expected.getId());
        }
        then(result.getAuthor()).isEqualTo(expected.getAuthor());
        then(result.getTitle()).isEqualTo(expected.getTitle());
        then(result.getPublished()).isEqualTo(expected.getPublished());
    }

    List<BookDTO> createSongs(int count) {
        List<BookDTO> songs = songDTOFactory.listBuilder().books(count, count);
        for (int i=0; i<songs.size(); i++) {
            BookDTO song = songs.get(i);
            if (i%2==0) {
                song.setAuthor("X" + song.getAuthor());
            }
            BookDTO createdSong = wtc.post()
                    .uri(BooksController.BOOKS_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(Mono.just(song), BookDTO.class)
                    .exchange()
                    .expectStatus().isCreated()
                    .returnResult(BookDTO.class)
                    .getResponseBody().blockFirst();
            songs.set(i, createdSong);
        }
        return songs;
    }
    BookDTO createSong() {
        return createSongs(1).stream().findFirst().get();
    }

    BookDTO get_song(String id) {
        return wtc.get()
                .uri(BooksController.BOOK_PATH, id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(BookDTO.class)
                .getResponseBody().blockFirst();
    }
}
