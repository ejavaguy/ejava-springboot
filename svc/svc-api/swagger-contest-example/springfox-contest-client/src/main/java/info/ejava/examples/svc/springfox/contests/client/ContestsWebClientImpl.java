package info.ejava.examples.svc.springfox.contests.client;

import info.ejava.examples.common.web.ServerConfig;
import info.ejava.examples.svc.springfox.contests.api.ContestAPI;
import info.ejava.examples.svc.springfox.contests.dto.ContestDTO;
import info.ejava.examples.svc.springfox.contests.dto.ContestListDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

public class ContestsWebClientImpl implements ContestAPI {
    private final URI baseUrl;
    private final RestTemplate restTemplate=null;
    private final WebClient webClient;
    private final MediaType mediaType;

    public ContestsWebClientImpl(WebClient webClient, ServerConfig serverConfig, String mediaType) {
        this.webClient = webClient;
        baseUrl = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl()).build().toUri();
        this.mediaType = MediaType.valueOf(mediaType);
    }

    public ContestsWebClientImpl(WebClient webClient, ServerConfig serverConfig) {
        this(webClient, serverConfig, MediaType.APPLICATION_JSON_VALUE);
    }
    

    @Override
    public Mono<ResponseEntity<ContestListDTO>> getContests(Integer offset, Integer limit) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUri(baseUrl).path(CONTESTS_PATH);
        if (offset!=null) {
            urlBuilder = urlBuilder.queryParam("offset", offset);
        }
        if (limit!=null) {
            urlBuilder = urlBuilder.queryParam("limit", limit);
        }
        URI url = urlBuilder.build().toUri();

        WebClient.RequestHeadersSpec<?> requestSpec = webClient.get()
                .uri(url)
                .accept(mediaType);

        return requestSpec.retrieve().toEntity(ContestListDTO.class);
    }

    @Override
    public Mono<ResponseEntity<ContestDTO>> createContest(ContestDTO quote) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(CONTESTS_PATH).build().toUri();

        WebClient.RequestHeadersSpec<?> requestSpec = webClient.post()
                .uri(url)
                .contentType(mediaType)
                .accept(mediaType)
                .bodyValue(quote);

        return requestSpec.retrieve().toEntity(ContestDTO.class);
    }

    @Override
    public Mono<ResponseEntity<Void>> updateContest(int id, ContestDTO quote) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(CONTEST_PATH).build(id);

        WebClient.RequestHeadersSpec<?> requestSpec = webClient.put()
                .uri(url)
                .contentType(mediaType)
                .bodyValue(quote);

        return requestSpec.retrieve().toEntity(Void.class);
    }

    @Override
    public Mono<ResponseEntity<Void>> containsContest(int id) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(CONTEST_PATH).build(id);

        WebClient.RequestHeadersSpec<?> requestSpec = webClient.head()
                .uri(url);

        return requestSpec.retrieve().toEntity(Void.class);
    }

    @Override
    public Mono<ResponseEntity<ContestDTO>> getContest(int id) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(CONTEST_PATH).build(id);

        WebClient.RequestHeadersSpec<?> requestSpec = webClient.get()
                .uri(url)
                .accept(mediaType);

        return requestSpec.retrieve().toEntity(ContestDTO.class);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteContest(int id) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(CONTEST_PATH).build(id);

        WebClient.RequestHeadersSpec<?> requestSpec = webClient.delete().uri(url);

        return requestSpec.retrieve().toEntity(Void.class);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteAllContests() {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(CONTESTS_PATH).build().toUri();

        WebClient.RequestHeadersSpec<?> requestSpec = webClient.delete().uri(url);

        return requestSpec.retrieve().toEntity(Void.class);
    }
}
