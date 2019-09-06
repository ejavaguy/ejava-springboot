package info.ejava.examples.svc.httpapi.gestures.svc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface GesturesService {
    @Getter
    @RequiredArgsConstructor
    public static class UpsertResult {
        private final boolean created;
        private final String previousValue;
    }

    public UpsertResult upsertGesture(String gestureType, String gesture);
    public String getGesture(String gestureType, String target);
    public void deleteGesture(String gestureType);
    public void deleteAllGestures();
}
