package info.ejava.examples.svc.httpapi.gestures.api;

import org.springframework.http.ResponseEntity;

/**
 * The following is an example of using the HTTP constructs,
 * Resources, and Methods consistent with the HTTP-based/REST-like
 * category of service discussed in the lectures.
 */
public interface GesturesAPI {
    public static final String GESTURES_PATH="api/gestures";
    public static final String GESTURE_PATH= "api/gestures/{gestureType}";
    public static final String target = "target";

    public ResponseEntity<String> upsertGesture(String gestureType, String gesture);

    public ResponseEntity<String> getGesture(String gestureType, String target);

    public ResponseEntity<Void> deleteGesture(String gestureType);

    public ResponseEntity<Void> deleteAllGestures();
}
