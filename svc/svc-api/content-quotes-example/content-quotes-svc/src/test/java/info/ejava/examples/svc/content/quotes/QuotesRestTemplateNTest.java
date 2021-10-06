package info.ejava.examples.svc.content.quotes;

import info.ejava.examples.svc.content.quotes.api.QuotesAPI;
import info.ejava.examples.svc.content.quotes.client.ServerConfig;
import info.ejava.examples.svc.content.quotes.dto.QuoteDTO;
import info.ejava.examples.svc.content.quotes.dto.QuoteDTOFactory;
import info.ejava.examples.svc.content.quotes.dto.QuoteListDTO;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;

/**
 * This test was put in place with RestTemplate so that we could leverage the
 * ability for RestTemplate filters to log payload bodies in a debug mode.
 */
@SpringBootTest(classes = {QuotesApplication.class, ClientTestConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "test=true")
@ActiveProfiles("test")
@Tag("springboot")
@Slf4j
public class QuotesRestTemplateNTest {
    @Autowired
    private QuoteDTOFactory quotesFactory;
    @Autowired
    private RestTemplate restTemplate;
    
    private URI baseUrl;
    private URI quotesUrl;
    private static MediaType[] mediaTypes = new MediaType[] {
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML
    };

    @BeforeEach
    public void setUp(@LocalServerPort int port) {
        ServerConfig serverConfig = new ServerConfig().withPort(port).build();
        baseUrl = serverConfig.getBaseUrl();
        quotesUrl = UriComponentsBuilder.fromUri(baseUrl).path(QuotesAPI.QUOTES_PATH).build().toUri();

        log.info("clearing all gestures");
        log.debug("DEBUG");
        restTemplate.delete(quotesUrl);
    }

    @AfterEach
    public void cleanUp() {
        //cut down on noise
        //restTemplate.delete(quotesUrl);
    }

    public static Stream<Arguments> mediaTypes() {
        List<Arguments> params = new ArrayList<>();
        for (MediaType contentType : mediaTypes) {
            for (MediaType acceptType : mediaTypes) {
                params.add(Arguments.of(contentType, acceptType));
            }
        }
        return params.stream();
    }

    @ParameterizedTest
    @MethodSource("mediaTypes")
    public void add_valid_quote_for_type(MediaType contentType, MediaType acceptType) {
        //given - valid quote
        QuoteDTO validQuote = quotesFactory.make();

        //when - making a request using different request and accept payload types
        RequestEntity request = RequestEntity.post(quotesUrl)
                .contentType(contentType)
                .accept(acceptType)
                .body(validQuote);
        ResponseEntity<QuoteDTO> response = restTemplate.exchange(request, QuoteDTO.class);

        //then the service will accept the format we supplied
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //and the content will be returned to us in the requested format
        then(response.getHeaders().getContentType()).isEqualTo(acceptType);

        //that equals what we sent, plus an ID generated
        QuoteDTO createdQuote = response.getBody();
        then(createdQuote).isEqualTo(validQuote.withId(createdQuote.getId()));
        //with a LOCATION response header referencing the URL for the created resource
        URI location = UriComponentsBuilder.fromUri(baseUrl).path(QuotesAPI.QUOTE_PATH).build(createdQuote.getId());
        then(response.getHeaders().getFirst(HttpHeaders.LOCATION)).isEqualTo(location.toString());
    }

    @Test
    public void get_quote() {
        //given - an existing/original quote
        QuoteDTO existingQuote = quotesFactory.make(); //hold onto the client-side object
        ResponseEntity<QuoteDTO> quoteResponse = restTemplate.postForEntity(quotesUrl, existingQuote, QuoteDTO.class);
        assertThat(quoteResponse.getStatusCode().series()).isEqualTo(HttpStatus.OK.series());
        int requestId = quoteResponse.getBody().getId();
        URI quoteUrl = UriComponentsBuilder.fromUri(baseUrl).path(QuotesAPI.QUOTE_PATH).build(requestId);
        RequestEntity request = RequestEntity.get(quoteUrl).build();

        //when - requesting quote by id
        ResponseEntity<QuoteDTO> response = restTemplate.exchange(request, QuoteDTO.class);

        //then ...
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isEqualTo(existingQuote.withId(requestId));
    }

    @ParameterizedTest
    @ValueSource(strings={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public void get_quotes(String mediaTypeString) {
        //given - an existing/original quote
        MediaType mediaType = MediaType.valueOf(mediaTypeString);
        Map<Integer, QuoteDTO> existingQuotes = new HashMap<>();
        QuoteListDTO quotes = quotesFactory.listBuilder().make(3, 3);
        for (QuoteDTO quote: quotes.getQuotes()) {
            RequestEntity request = RequestEntity.post(quotesUrl).body(quote);
            ResponseEntity<QuoteDTO> quoteResponse = restTemplate.exchange(request, QuoteDTO.class);
            assertThat(quoteResponse.getStatusCode().series()).isEqualTo(HttpStatus.OK.series());
            QuoteDTO addedQuote = quoteResponse.getBody();
            existingQuotes.put(addedQuote.getId(), addedQuote);
        }
        BDDAssertions.assertThat(existingQuotes).isNotEmpty();
        URI quoteUrl = UriComponentsBuilder.fromUri(baseUrl).path(QuotesAPI.QUOTES_PATH).build().toUri();

        //when - requesting quote by id
        ResponseEntity<QuoteListDTO> response = restTemplate.exchange(
                RequestEntity.get(quoteUrl).accept(mediaType).build(),
                QuoteListDTO.class);

        //then ...
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        QuoteListDTO quotesPage = response.getBody();
        then(quotes.getCount()).isEqualTo(existingQuotes.size());
        for (QuoteDTO q: quotesPage.getQuotes()) {
            then(existingQuotes.remove(q.getId())).isNotNull();
        }
        then(existingQuotes).isEmpty();
    }
}
