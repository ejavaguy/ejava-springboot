package info.ejava.examples.svc.httpapi.gestures.client;

import info.ejava.examples.svc.httpapi.gestures.ServerConfig;
import info.ejava.examples.svc.httpapi.gestures.api.GesturesAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * This class demonstrates a synchronous/blocking client using RestTemplate
 * -- which is the only mode RestTemplate can operate in. Both the client and
 * server-side are implemented synchronously -- so we are able to naively
 * leverage a common interface.
 */
@Slf4j
@Qualifier("template")
public class GesturesAPITemplateClient implements GesturesAPI {
    private final URI baseUrl;
    private final RestTemplate restTemplate;

    public GesturesAPITemplateClient(RestTemplate restTemplate, ServerConfig serverConfig) {
        this.restTemplate = restTemplate;
        baseUrl = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl()).build().toUri();
    }

    @Override
    public ResponseEntity<Void> deleteAllGestures() {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(GESTURES_PATH).build().toUri();

        RequestEntity<Void> request = RequestEntity.delete(url).build();

        ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);
        return response;
    }

    @Override
    public ResponseEntity<String> upsertGesture(String gestureType, String gesture) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(GESTURE_PATH).build(gestureType);

        RequestEntity<String> request = RequestEntity.post(url)
                .accept(MediaType.TEXT_PLAIN)
                .contentType(MediaType.TEXT_PLAIN)
                .body(gesture);

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        return response;
    }

    @Override
    public ResponseEntity<String> getGesture(String gestureType, String target) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(baseUrl).path(GESTURE_PATH);
        if (target!=null) {
            builder = builder.queryParam("target", target);
        }
        URI url = builder.build(gestureType);

        RequestEntity<Void> request = RequestEntity.get(url)
                .accept(MediaType.TEXT_PLAIN)
                .build();

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        return response;
    }

    @Override
    public ResponseEntity<Void> deleteGesture(String gestureType) {
        URI url = UriComponentsBuilder.fromUri(baseUrl).path(GESTURE_PATH).build(gestureType);

        RequestEntity<Void> request = RequestEntity.delete(url).build();

        ResponseEntity response = restTemplate.exchange(request, Void.class);

        return response;
    }
}
