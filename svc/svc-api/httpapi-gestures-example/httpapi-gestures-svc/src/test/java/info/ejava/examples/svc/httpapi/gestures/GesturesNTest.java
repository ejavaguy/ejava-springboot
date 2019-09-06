package info.ejava.examples.svc.httpapi.gestures;

import info.ejava.examples.svc.httpapi.GesturesApplication;
import info.ejava.examples.svc.httpapi.gestures.controllers.ExceptionAdvice;
import info.ejava.examples.svc.httpapi.gestures.controllers.GesturesController;
import info.ejava.examples.svc.httpapi.gestures.svc.ClientErrorException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

/**
 * This test is an example of an integration unit test where we leverage
 * the Spring context to provide us with the components of the application
 * but issue the commands directly to the controller class as if it were
 * a POJO.
 */
@SpringBootTest(classes={GesturesApplication.class})
@ActiveProfiles("test")
@Tag("springboot")
@DisplayName("Gestures Integration Unit Test")
@Slf4j
public class GesturesNTest {
    @Autowired
    private GesturesController gesturesController;
    @Autowired
    private ExceptionAdvice exceptionAdvice;

    private String currentRequestUrl;

    @BeforeEach
    public void setUp() {
        currentRequestUrl = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        gesturesController.deleteAllGestures();
    }

    @Test
    public void add_new_gesture() {
        //when - adding a new gesture
        ResponseEntity<String> response = gesturesController.upsertGesture("hello", "hi");

        //then - it will be accepted and nothing returned
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        then(response.getBody()).isNull();
        then(response.getHeaders().getFirst(HttpHeaders.LOCATION)).isEqualTo(currentRequestUrl);
    }

    @Test
    public void replace_gesture() {
        //when - we update the first time
        ResponseEntity<String> response = gesturesController.upsertGesture("hello","hi");

        //then -- gesture accepted and nothing returned
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        then(response.getBody()).isNull();

        //when - an existing value gets updated
        response = gesturesController.upsertGesture("hello","howdy");

        //then - it gets accepted and initial gesture back in response
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isEqualTo("hi");
        then(response.getHeaders().getFirst(HttpHeaders.LOCATION)).isNull();
    }


    @Test
    public void get_unknown_gesture_type() {
        //when - requesting an unknown gesture
            //NOTE: advice is not being applied to injected controller in this type of test
        ClientErrorException.NotFoundException ex = catchThrowableOfType(()->
                gesturesController.getGesture("unknown", null),
                ClientErrorException.NotFoundException.class);

        //then - not found will be returned
        ResponseEntity response = exceptionAdvice.handle(ex);
        then(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        then(response.getBody().toString()).contains("unknown");
        then(response.getHeaders().getFirst(HttpHeaders.CONTENT_LOCATION)).isNull();
    }

    @Test
    public void get_gesture_without_target() {
        //given - we have a known gesture present
        ResponseEntity<String> result = gesturesController.upsertGesture("hello","howdy");
        then(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //when - requesting a known gesture
        ResponseEntity<String> response = gesturesController.getGesture("hello", null);

        //then - gesture will be returned without target
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody().toString()).contains("howdy");
        then(response.getHeaders().getFirst(HttpHeaders.CONTENT_LOCATION)).isEqualTo(currentRequestUrl);
    }

    @Test
    public void get_gesture_with_target() {
        //given - we have a known gesture present
        ResponseEntity<String> result = gesturesController.upsertGesture("hello","howdy");
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //when - requesting a known gesture
        ResponseEntity<String> response = gesturesController.getGesture("hello", "jim");

        //then - gesture will be returned with target added
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody().toString()).contains("howdy, jim");
        then(response.getHeaders().getFirst(HttpHeaders.CONTENT_LOCATION)).isEqualTo(currentRequestUrl);
    }

    @Test
    public void delete_unknown_gesture() {
        //when - deleting unknown gesture
        ResponseEntity<Void> response = gesturesController.deleteGesture("unknown");

        //then - will receive success with no content
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void delete_known_gesture() {
        //given - we have a known gesture present
        ResponseEntity<String> result = gesturesController.upsertGesture("hello","howdy");
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //when - deleting known gesture
        ResponseEntity<Void> response = gesturesController.deleteGesture("hello");

        //then - will receive success with no content
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void delete_all_gestures() {
        //when
        ResponseEntity<Void> response = gesturesController.deleteAllGestures();
        //then - collection was cleared
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
