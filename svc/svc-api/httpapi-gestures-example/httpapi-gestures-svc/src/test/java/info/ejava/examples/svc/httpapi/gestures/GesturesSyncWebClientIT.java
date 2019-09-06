package info.ejava.examples.svc.httpapi.gestures;

import info.ejava.examples.svc.httpapi.GesturesApplication;
import info.ejava.examples.svc.httpapi.gestures.api.GesturesAPI;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

/**
 * This class is an example of an integration test that uses full HTTP
 * communications and a convenient client command wrapper.
 */
@SpringBootTest(classes = {GesturesApplication.class, ClientTestConfiguration.class})
@ActiveProfiles("test")
@Tag("springboot")
@DisplayName("Gestures Remote Client IT")
@Slf4j
public class GesturesSyncWebClientIT {
    @Autowired
    @Qualifier("webclient")
    private GesturesAPI gesturesClient;

    @BeforeEach
    public void setUp() {
        log.info("clearing all gestures");
        gesturesClient.deleteAllGestures();
    }

    @Test
    public void add_new_gesture() {
        //when - adding a new gesture
        ResponseEntity<?> response = gesturesClient.upsertGesture("hello", "hi");

        //then - it will be accepted and nothing returned
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        then(response.getBody()).isNull();
    }

    @Test
    public void replace_gesture() {
        //when - we update the first time
        ResponseEntity<?> response = gesturesClient.upsertGesture("hello","hi");

        //then -- gesture accepted and nothing returned
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        then(response.getBody()).isNull();

        //when - an existing value gets updated
        response = gesturesClient.upsertGesture("hello","howdy");
        response.getBody();

        //then - it gets accepted and initial gesture back in response
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isEqualTo("hi");
    }

    @Test
    public void get_unknown_gesture_type() throws Exception {
        //when - requesting an unknown gesture
        WebClientResponseException ex = catchThrowableOfType(
                () -> gesturesClient.getGesture("unknown", null),
                WebClientResponseException.NotFound.class);

        //then - not found will be returned
        then(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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
        WebClientResponseException.NotFound ex = catchThrowableOfType(
                () -> gesturesClient.getGesture("hello", null),
                WebClientResponseException.NotFound.class);
        then(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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
            WebClientResponseException ex = Assertions.catchThrowableOfType(
                    ()->gesturesClient.getGesture(gestureType, null),
                    WebClientResponseException.NotFound.class);
            then(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}
