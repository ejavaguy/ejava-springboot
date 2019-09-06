package info.ejava.examples.svc.httpapi.gestures.client;

import info.ejava.examples.svc.httpapi.gestures.ServerConfig;
import info.ejava.examples.svc.httpapi.gestures.api.GesturesAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * This class demonstrates an implementation of the Gestures client using
 * WebClient in a purely *synchronous* / *blocking* manner. The primary
 * difference between this and the RestTemplate version is the specific
 * types of exceptions thrown. Even though they are logically equivalent
 * (i.e., NotFound, BadRequest, etc.), they are from two different
 * class hierarchies.
 */
@Slf4j
@Qualifier("webclient")
public class GesturesAPISyncWebClient implements GesturesAPI {
    private final URI baseUrl;
    private final RestTemplate restTemplate=null;
    private final WebClient webClient;

    public GesturesAPISyncWebClient(WebClient webClient, ServerConfig serverConfig) {
        this.webClient = webClient;
        baseUrl = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl()).build().toUri();
    }

    @Override
    public ResponseEntity<String> upsertGesture(String gestureType, String gesture) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(GESTURE_PATH).build(gestureType);

        WebClient.RequestHeadersSpec<?> request = webClient.post()
                .uri(url)
                .accept(MediaType.TEXT_PLAIN)
                .contentType(MediaType.TEXT_PLAIN)
                .body(Mono.just(gesture), String.class);
        Mono<ResponseEntity<String>> response = request.retrieve().toEntity(String.class);
        return response.block();
    }

    @Override
    public ResponseEntity<String> getGesture(String gestureType, String target) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(baseUrl).path(GESTURE_PATH);
        if (target!=null) {
            builder = builder.queryParam("target", target);
        }
        URI url = builder.build(gestureType);

        WebClient.RequestHeadersSpec<?> request = webClient
                .get()
                .uri(url)
                .accept(MediaType.TEXT_PLAIN);
        Mono<ResponseEntity<String>> response = request.retrieve().toEntity(String.class);
        return response.block();
    }

    @Override
    public ResponseEntity<Void> deleteAllGestures() {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(GESTURES_PATH).build().toUri();
        return doDelete(url);
    }

    @Override
    public ResponseEntity<Void> deleteGesture(String gestureType) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(GESTURE_PATH).build(gestureType);
        return doDelete(url);
    }

    public ResponseEntity<Void> doDelete(URI url) {
        WebClient.RequestHeadersSpec<?> request = webClient
                .delete()
                .uri(url);
        Mono<ResponseEntity<Void>> response = request.retrieve().toEntity(Void.class);
        return response.block();
    }
}
