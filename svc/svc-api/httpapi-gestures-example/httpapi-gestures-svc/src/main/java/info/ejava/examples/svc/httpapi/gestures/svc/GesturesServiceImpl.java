package info.ejava.examples.svc.httpapi.gestures.svc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class GesturesServiceImpl implements GesturesService {
    private Map<String, String> gestures = new HashMap<>();

    @Override
    public UpsertResult upsertGesture(String gestureType, String gesture) {
        //data access method
        String previousGesture = gestures.put(gestureType,gesture);
        log.debug("set gesture({}) to {}, returning previous value {}",
                gestureType, gesture, previousGesture);

        return new UpsertResult(previousGesture==null, previousGesture);
    }

    @Override
    public String getGesture(String gestureType, String target) {
        //data access method
        String gesture = gestures.get(gestureType);

        if (gesture==null) {
            log.debug("gestureType[{}] not found", gestureType);
            throw new ClientErrorException
                    .NotFoundException("gesture type[%s] not found", gestureType);
        } else {
            String response = gesture + (target==null ? "" : ", " + target);
            log.debug("{} gesture returning {} for {}", gestureType, response, target);
            return response;
        }
    }

    @Override
    public void deleteGesture(String gestureType) {
        //data access method
        String gesture = gestures.remove(gestureType);

        log.debug("removed gesture({}) - was {}", gestureType, gesture);
    }

    @Override
    public void deleteAllGestures() {
        log.debug("removing all({}) gestures", gestures.size());

        //data access method
        gestures.clear();
    }
}
