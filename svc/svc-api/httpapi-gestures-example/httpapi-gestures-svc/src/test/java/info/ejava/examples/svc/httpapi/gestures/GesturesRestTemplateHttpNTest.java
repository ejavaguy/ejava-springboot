package info.ejava.examples.svc.httpapi.gestures;

import info.ejava.examples.svc.httpapi.GesturesApplication;
import info.ejava.examples.svc.httpapi.gestures.api.GesturesAPI;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This class is an example unit integration test of a controller class.
 * There is a Spring context that creates components under test that
 * we communicate with thru HTTP. There is coordination within Spring Boot
 * to stand up the pseudo-server and supply the port information for this
 * client to contact. Just know -- the controller and components are
 * operating with a separate Spring context.
 */
@SpringBootTest(classes={GesturesApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Tag("springboot")
@DisplayName("Gestures Http Integration Test")
@Slf4j
@Tag("springboot")
public class GesturesRestTemplateHttpNTest {
    @LocalServerPort
    private int port;
    private ServerConfig serverConfig;

    @Autowired
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        serverConfig = new ServerConfig().withPort(port).build();
        URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path(GesturesAPI.GESTURES_PATH).build().toUri();

        RequestEntity request = RequestEntity.delete(url).build();
        log.info("calling {}", request);

        restTemplate.exchange(request, Void.class);
    }


    @Test
    public void add_new_gesture() {
        //given
        URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path(GesturesAPI.GESTURE_PATH).build("hello");
        RequestEntity<String> request = RequestEntity.post(url)
                .accept(MediaType.TEXT_PLAIN)
                .contentType(MediaType.TEXT_PLAIN)
                .body("hi");

        //when - adding a new gesture
        log.info("calling {}", request);
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        //then - it will be accepted and nothing returned
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        then(response.getBody()).isNull();
        then(response.getHeaders().getFirst(HttpHeaders.LOCATION))
                .isEqualTo(url.toString());
    }

    @Test
    public void replace_gesture() {
        //given
        URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path(GesturesAPI.GESTURE_PATH).build("hello");
        RequestEntity<String> request = RequestEntity.post(url)
                .accept(MediaType.TEXT_PLAIN)
                .contentType(MediaType.TEXT_PLAIN)
                .body("hi");

        //when - we update the first time
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        //then -- gesture accepted and nothing returned
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        then(response.getBody()).isNull();

        //when - an existing value gets updated
        request = RequestEntity.post(url)
                .accept(MediaType.TEXT_PLAIN)
                .contentType(MediaType.TEXT_PLAIN)
                .body("howdy");
        response = restTemplate.exchange(request, String.class);

        //then - it gets accepted and initial gesture back in response
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isEqualTo("hi");
        then(response.getHeaders().getFirst(HttpHeaders.LOCATION)).isNull();
    }


    @Test
    public void get_unknown_gesture_type() throws Exception {
        //given - unknown gesture
        URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path(GesturesAPI.GESTURE_PATH).build("unknown");
        RequestEntity<Void> request = RequestEntity.get(url)
                .accept(MediaType.TEXT_PLAIN)
                .build();

        //when - requesting an unknown gesture
        RestClientResponseException ex = assertThrows(RestClientResponseException.class,
                ()->restTemplate.exchange(request, String.class));

        //then - not found will be returned
        then(ex.getRawStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        then(ex.getResponseBodyAsString()).contains("unknown");
        then(ex.getResponseHeaders().getFirst(HttpHeaders.LOCATION)).isNull();
    }

    @Test
    public void get_gesture_without_target() throws Exception {
        //given - we have a known gesture present
        URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path(GesturesAPI.GESTURE_PATH).build("hello");
        RequestEntity<String> postRequest = RequestEntity.post(url)
                .accept(MediaType.TEXT_PLAIN)
                .contentType(MediaType.TEXT_PLAIN)
                .body("howdy");
        ResponseEntity<String> response = restTemplate.exchange(postRequest, String.class);
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //when - requesting a known gesture
        RequestEntity<Void> getRequest = RequestEntity.get(url)
                .accept(MediaType.TEXT_PLAIN)
                .build();
        response = restTemplate.exchange(getRequest, String.class);

        //then - gesture will be returned without target
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isEqualTo("howdy");
        then(response.getHeaders().getFirst(HttpHeaders.CONTENT_LOCATION))
                .isEqualTo(url.toString());
    }

    @Test
    public void get_gesture_with_target() throws Exception {
        //given - we have a known gesture present
        URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path(GesturesAPI.GESTURE_PATH).build("hello");
        RequestEntity<?> postRequest = RequestEntity.post(url)
                .accept(MediaType.TEXT_PLAIN)
                .contentType(MediaType.TEXT_PLAIN)
                .body("howdy");
        ResponseEntity<String> response = restTemplate.exchange(postRequest, String.class);
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //when - requesting a known gesture
        url = UriComponentsBuilder.fromUri(url).queryParam("target", "jim").build().toUri();
        RequestEntity<Void> getRequest = RequestEntity.get(url)
                .accept(MediaType.TEXT_PLAIN)
                .build();
        response = restTemplate.exchange(getRequest, String.class);

        //then - gesture will be returned with target added
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isEqualTo("howdy, jim");
        then(response.getHeaders().getFirst(HttpHeaders.CONTENT_LOCATION))
                .isEqualTo(url.toString());
    }

    @Test
    public void delete_unknown_gesture() throws Exception {
        //given
        URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path(GesturesAPI.GESTURE_PATH).build("unknown");
        RequestEntity<Void> request = RequestEntity.delete(url).build();

        //when - deleting unknown gesture
        ResponseEntity<?> response = restTemplate.exchange(request, String.class);

        //then - will receive success with no content
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        then(response.getBody()).isNull();
    }

    @Test
    public void delete_known_gesture() throws Exception {
        //given - we have a known gesture present
        URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path(GesturesAPI.GESTURE_PATH).build("hello");
        RequestEntity<String> postRequest = RequestEntity.post(url)
                .accept(MediaType.TEXT_PLAIN)
                .contentType(MediaType.TEXT_PLAIN)
                .body("howdy");
        ResponseEntity<String> postResponse = restTemplate.exchange(postRequest, String.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //when - deleting known gesture
        RequestEntity<Void> request = RequestEntity.delete(url).build();
        ResponseEntity<?> response = restTemplate.exchange(request, String.class);

        //then - will receive success with no content
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        //and then the gestureType will be unknown
        RequestEntity<Void> getRequest = RequestEntity.get(url)
                .accept(MediaType.TEXT_PLAIN)
                .build();
        //restTemplate throws exception for non-200 responses
        RestClientResponseException ex = assertThrows(RestClientResponseException.class,
                () -> restTemplate.exchange(getRequest, String.class));
        then(ex.getRawStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void delete_all_gestures() throws Exception {
        //given
        List<String> gestureTypes = Arrays.asList("hello", "goodbye");
        for (String gestureType : gestureTypes) {
            URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                    .path(GesturesAPI.GESTURE_PATH).build(gestureType);
            RequestEntity<?> postRequest = RequestEntity.post(url)
                    .accept(MediaType.TEXT_PLAIN)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("aloha");
            ResponseEntity<String> response = restTemplate.exchange(postRequest, String.class);
            then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        //when deleting all gestures
        URI url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path(GesturesAPI.GESTURES_PATH).build().toUri();
        RequestEntity<Void> request = RequestEntity.delete(url).build();
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        //then - collection was cleared
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        //and then no gestures left
        for (String gestureType : gestureTypes) {
            url = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                    .path(GesturesAPI.GESTURE_PATH).build(gestureType);
            RequestEntity<Void> getRequest = RequestEntity.get(url)
                    .accept(MediaType.TEXT_PLAIN)
                    .build();
                //restTemplate throws exception for non-200 responses
            RestClientResponseException ex = catchThrowableOfType(
                    () -> restTemplate.exchange(getRequest, String.class),
                    RestClientResponseException.class);
            then(ex.getRawStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
    }

}
