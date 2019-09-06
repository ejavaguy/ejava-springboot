package info.ejava.examples.svc.httpapi.gestures.controllers;

import info.ejava.examples.svc.httpapi.gestures.api.GesturesAPI;
import info.ejava.examples.svc.httpapi.gestures.svc.GesturesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static info.ejava.examples.svc.httpapi.gestures.svc.GesturesService.UpsertResult;

/**
 * The following is an example of using the HTTP constructs,
 * Resources, and Methods consistent with the HTTP-based/REST-like
 * category of service discussed in the lectures. To keep it
 * simple, we are using a simple Map object to act as our
 * back-end business service and storage. The WebMvc mappings
 * have been assigned to the interface.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class GesturesController implements GesturesAPI {
    //simplistic business service
    private final GesturesService gestures;

    @RequestMapping(path=GESTURE_PATH,
            method={RequestMethod.POST, RequestMethod.PUT},
            consumes = {MediaType.TEXT_PLAIN_VALUE},
            produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> upsertGesture(
            @PathVariable(name="gestureType", required=true) String gestureType,
            @RequestBody String gesture) {
        //business method
        UpsertResult result = gestures.upsertGesture(gestureType,gesture);

        log.debug("set gesture({}) to {}, returning previous value {}",
                gestureType, gesture, result.getPreviousValue());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        ResponseEntity.BodyBuilder response = result.isCreated() ?
            ResponseEntity.created(location) :
            ResponseEntity.status(HttpStatus.OK);

        return response.body(result.getPreviousValue());
    }

    @RequestMapping(path=GESTURE_PATH,
            method=RequestMethod.GET,
            produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> getGesture(
            @PathVariable(name="gestureType") String gestureType,
            @RequestParam(name="target", required=false) String target) {
        //business method
        String result = gestures.getGesture(gestureType, target);

        String location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_LOCATION, location)
                .body(result);
    }

    @RequestMapping(path=GESTURE_PATH,
            method=RequestMethod.DELETE)
    public ResponseEntity<Void> deleteGesture(
            @PathVariable(value = "gestureType") String gestureType) {
        //business method
        gestures.deleteGesture(gestureType);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path=GESTURES_PATH,
            method=RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllGestures() {
        //business method
        gestures.deleteAllGestures();

        return ResponseEntity.noContent().build();
    }
}
