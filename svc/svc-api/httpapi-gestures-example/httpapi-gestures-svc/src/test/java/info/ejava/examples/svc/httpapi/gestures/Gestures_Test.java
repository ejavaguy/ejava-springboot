package info.ejava.examples.svc.httpapi.gestures;

import info.ejava.examples.svc.httpapi.gestures.controllers.ExceptionAdvice;
import info.ejava.examples.svc.httpapi.gestures.controllers.GesturesController;
import info.ejava.examples.svc.httpapi.gestures.svc.ClientErrorException.NotFoundException;
import info.ejava.examples.svc.httpapi.gestures.svc.GesturesService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static info.ejava.examples.svc.httpapi.gestures.svc.GesturesService.UpsertResult;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.and;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * This class is an example unit test of a controller class. There is
 * no Spring context instantiated and all dependencies have been mocked.
 */
@ExtendWith(MockitoExtension.class)
@Slf4j
public class Gestures_Test {
    @InjectMocks
    private GesturesController gesturesController;

    @Mock
    private GesturesService gesturesService;

    @Captor
    private ArgumentCaptor<String> stringArg;

    private String currentRequestUrl;
    private ExceptionAdvice errorAdvice = new ExceptionAdvice();

    @BeforeEach
    public void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        currentRequestUrl = ServletUriComponentsBuilder.fromRequest(request).build().toUriString();

        gesturesController.deleteAllGestures();
        reset(gesturesService); //don't bleed into test methods
    }

    @Test
    public void add_new_gesture() {
        //given a service with no current gesture
        given(gesturesService.upsertGesture("hello","hi"))
                .willReturn(new UpsertResult(true, null));

        //when - adding a new gesture
        ResponseEntity<String> response =
                gesturesController.upsertGesture("hello", "hi");

        //then - it will be accepted and nothing returned
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        then(response.getBody()).isNull();
        then(response.getHeaders().getFirst(HttpHeaders.LOCATION)).isEqualTo(currentRequestUrl);
    }

    @Test
    public void replace_gesture() {
        //given -- that our mock business service will return "hi" as previous gesture
        given(gesturesService.upsertGesture(anyString(), anyString())).willReturn(
                new UpsertResult(true, null),
                new UpsertResult(false, "hi"));

        //when - we update the first time
        ResponseEntity<String> response =
                gesturesController.upsertGesture("hello","hi");

        //then -- gesture accepted and nothing returned
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        then(response.getBody()).isNull();

        //when - an existing value gets updated
        response = gesturesController.upsertGesture("hello","howdy");

        //then - it gets accepted and initial gesture back in response
        BDDMockito.then(gesturesService).should(times(2))
                .upsertGesture(anyString(), anyString());
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isEqualTo("hi");
        then(response.getHeaders().getFirst(HttpHeaders.LOCATION)).isNull();
    }

    @Test
    public void get_unknown_gesture_type() {
        //given - a gesture does not exist
        given(gesturesController.getGesture("unknown", null))
                .willThrow(new NotFoundException("unknown gestureType[unknown]"));

        //when - requesting an unknown gesture
        NotFoundException ex = catchThrowableOfType(()->
                gesturesController.getGesture("unknown", null),
                NotFoundException.class);

        //then - not found will be returned
        ResponseEntity<String> response = errorAdvice.handle(ex);
        then(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        then(response.getBody().toString()).contains("unknown");
        then(response.getHeaders().getFirst(HttpHeaders.CONTENT_LOCATION)).isNull();
    }

    @Test
    public void get_gesture_without_target() {
        //given - we have a known gesture present
        given(gesturesService.getGesture("hello", null)).willReturn("howdy");

        //when - requesting a known gesture
        ResponseEntity<String> response = gesturesController.getGesture("hello", null);

        //then - gesture will be returned without target
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody().toString()).contains("howdy");
        then(response.getHeaders().getFirst(HttpHeaders.CONTENT_LOCATION))
                .isEqualTo(currentRequestUrl);
    }

    @Test
    public void get_gesture_with_target() {
        //given - we have a known gesture present
        given(gesturesService.getGesture("hello", "jim")).willReturn("howdy, jim");

        //when - requesting a known gesture
        ResponseEntity<String> response = gesturesController.getGesture("hello", "jim");

        //then - gesture will be returned with target added
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody().toString()).contains("howdy, jim");
        then(response.getHeaders().getFirst(HttpHeaders.CONTENT_LOCATION))
                .isEqualTo(currentRequestUrl);
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
        //when - deleting known gesture
        ResponseEntity<Void> response = gesturesController.deleteGesture("hello");

        //then - gestureType will have been removed from map
        BDDMockito.then(gesturesService).should(times(1)).deleteGesture("hello");
        //and then - will receive success with no content
        and.then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
    
    @Test
    public void delete_all_gestures() {
        //when
        ResponseEntity<Void> response = gesturesController.deleteAllGestures();
        //then - collection was cleared
        BDDMockito.then(gesturesService).should(times(1)).deleteAllGestures();
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
