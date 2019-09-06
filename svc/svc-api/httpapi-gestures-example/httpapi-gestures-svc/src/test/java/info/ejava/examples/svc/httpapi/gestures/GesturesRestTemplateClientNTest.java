package info.ejava.examples.svc.httpapi.gestures;

import info.ejava.examples.svc.httpapi.GesturesApplication;
import info.ejava.examples.svc.httpapi.gestures.api.GesturesAPI;
import info.ejava.examples.svc.httpapi.gestures.client.GesturesAPITemplateClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This class is an example of a unit integration test that uses full HTTP
 * communications and a convenient client command wrapper.
 */
@SpringBootTest(classes = {GesturesApplication.class, ClientTestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Tag("springboot")
@DisplayName("Gestures Client Integration Unit Test")
@Slf4j
public class GesturesRestTemplateClientNTest {
    private GesturesAPI gesturesClient;

    @BeforeEach
    public void setUp(@LocalServerPort int port,
                      @Autowired RestTemplate restTemplate) {
        ServerConfig serverConfig = new ServerConfig().withPort(port).build();
        gesturesClient = new GesturesAPITemplateClient(restTemplate, serverConfig);

        log.info("clearing all gestures");
        log.debug("DEBUG");
        gesturesClient.deleteAllGestures();
    }

    @Test
    public void add_new_gesture() {
        //when - adding a new gesture
        ResponseEntity<String> response = gesturesClient.upsertGesture("hello", "hi");

        //then - it will be accepted and nothing returned
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        then(response.getBody()).isNull();
    }

    @Test
    public void replace_gesture() {
        //when - we update the first time
        ResponseEntity<?> result = gesturesClient.upsertGesture("hello","hi");

        //then -- gesture accepted and nothing returned
        then(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        then(result.getBody()).isNull();

        //when - an existing value gets updated
        result = gesturesClient.upsertGesture("hello","howdy");
        result.getBody();

        //then - it gets accepted and initial gesture back in response
        then(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(result.getBody()).isEqualTo("hi");
    }

    @Test
    public void get_unknown_gesture_type() throws Exception {
        //when - requesting an unknown gesture
        RestClientResponseException ex = assertThrows(RestClientResponseException.class,
                ()->gesturesClient.getGesture("unknown", null));

        //then - not found will be returned
        then(ex.getRawStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        then(ex.getResponseBodyAsString()).contains("unknown");
    }

    @Test
    public void get_gesture_without_target() throws Exception {
        //given - we have a known gesture present
        ResponseEntity<String> response = gesturesClient.upsertGesture("hello","howdy");
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //when - requesting a known gesture
        response = gesturesClient.getGesture("hello", null);

        //then - gesture will be returned without target
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isEqualTo("howdy");
    }

    @Test
    public void get_gesture_with_target() throws Exception {
        //given - we have a known gesture present
        ResponseEntity<String> response = gesturesClient.upsertGesture("hello", "howdy");
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //when - requesting a known gesture
        response = gesturesClient.getGesture("hello", "jim");

        //then - gesture will be returned with target added
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isEqualTo("howdy, jim");
    }

    @Test
    public void delete_unknown_gesture() throws Exception {
        //given
        //when - deleting unknown gesture
        ResponseEntity<?> response = gesturesClient.deleteGesture("unknown");

        //then - will receive success with no content
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        then(response.getBody()).isNull();
    }

    @Test
    public void delete_known_gesture() throws Exception {
        //given - we have a known gesture present
        gesturesClient.upsertGesture("hello", "howdy");

        //when - deleting known gesture
        ResponseEntity<?> response = gesturesClient.deleteGesture("hello");

        //then - will receive success with no content
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        //and then the gestureType will be unknown
            //restTemplate throws exception for non-200 responses
        RestClientResponseException ex = assertThrows(RestClientResponseException.class,
                () -> gesturesClient.getGesture("hello", null));
        then(ex.getRawStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void delete_all_gestures() throws Exception {
        //given
        List<String> gestureTypes = Arrays.asList("hello", "goodbye");
        for (String gestureType : gestureTypes) {
            ResponseEntity<String> response = gesturesClient.upsertGesture(gestureType, "aloha");
            then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        //when deleting all gestures
        ResponseEntity<Void> response = gesturesClient.deleteAllGestures();

        //then - collection was cleared
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        //and then no gestures left
        for (String gestureType : gestureTypes) {
                //restTemplate throws exception for non-200 responses
            RestClientResponseException ex = catchThrowableOfType(
                    () -> gesturesClient.getGesture(gestureType, null),
                    RestClientResponseException.class);
            then(ex.getRawStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
    }
}
