package info.ejava.examples.svc.content.quotes.client;

import info.ejava.examples.svc.content.quotes.api.QuotesAPI;
import info.ejava.examples.svc.content.quotes.dto.QuoteDTO;
import info.ejava.examples.svc.content.quotes.dto.QuoteListDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
public class QuotesAPIClient implements QuotesAPI {
    private final URI baseUrl;
    private final WebClient webClient;
    private final MediaType mediaType;

    public QuotesAPIClient(WebClient webClient, ServerConfig serverConfig, String mediaType) {
        this.webClient = webClient;
        baseUrl = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl()).build().toUri();
        this.mediaType = MediaType.valueOf(mediaType);
    }

    public QuotesAPIClient(WebClient webClient, ServerConfig serverConfig) {
        this(webClient, serverConfig, MediaType.APPLICATION_JSON_VALUE);
    }

    @Override
    public Mono<ResponseEntity<QuoteDTO>> createQuote(QuoteDTO quote) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(QUOTES_PATH).build().toUri();

        WebClient.RequestHeadersSpec<?> request = webClient.post()
                .uri(url)
                .contentType(mediaType)
                .body(Mono.just(quote), QuoteDTO.class)
                .accept(mediaType);

        return request.retrieve().toEntity(QuoteDTO.class);
    }

    @Override
    public Mono<ResponseEntity<Void>> updateQuote(int id, QuoteDTO quote) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(QUOTE_PATH).build(id);

        WebClient.RequestHeadersSpec<?> request = webClient.put()
                .uri(url)
                .contentType(mediaType)
                .body(Mono.just(quote), QuoteDTO.class);
        return request.retrieve().toEntity(Void.class);
    }

    @Override
    public Mono<ResponseEntity<Void>> containsQuote(int id) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(QUOTE_PATH).build(id);

        WebClient.RequestHeadersSpec<?> request = webClient.head()
                .uri(url);
        return request.retrieve().toEntity(Void.class);
    }

    @Override
    public Mono<ResponseEntity<QuoteDTO>> getQuote(int id) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(QUOTE_PATH).build(id);

        WebClient.RequestHeadersSpec<?> request = webClient.get()
                .uri(url)
                .accept(mediaType);
        return request.retrieve().toEntity(QuoteDTO.class);
    }

    @Override
    public Mono<ResponseEntity<QuoteDTO>> randomQuote() {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(RANDOM_QUOTE_PATH).build().toUri();

        WebClient.RequestHeadersSpec<?> request = webClient.get()
                .uri(url)
                .accept(mediaType);
        return request.retrieve().toEntity(QuoteDTO.class);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteQuote(int id) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(QUOTE_PATH).build(id);
        return doDelete(url);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteAllQuotes() {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(QUOTES_PATH).build().toUri();
        return doDelete(url);
    }

    protected Mono<ResponseEntity<Void>> doDelete(URI url) {
        WebClient.RequestHeadersSpec<?> request = webClient.delete()
                .uri(url);
        WebClient.ResponseSpec responseSpec = request.retrieve();
        return responseSpec.toEntity(Void.class);
    }

    @Override
    public Mono<ResponseEntity<QuoteListDTO>> getQuotes(Integer offset, Integer limit) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUri(baseUrl).path(QUOTES_PATH);
        if (offset!=null) {
            urlBuilder = urlBuilder.queryParam("offset", offset);
        }
        if (limit!=null) {
            urlBuilder = urlBuilder.queryParam("limit", limit);
        }
        URI url = urlBuilder.build().toUri();

        WebClient.RequestHeadersSpec<?> request = webClient.get().uri(url);
        WebClient.ResponseSpec responseSpec = request.retrieve();
        return responseSpec.toEntity(QuoteListDTO.class);
    }
}
