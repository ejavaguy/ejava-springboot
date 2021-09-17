package info.ejava.assignments.api.race.client.races;

import org.springframework.http.ResponseEntity;

public interface RacesAPI {
    public static final String RACES_PATH="/api/races";
    public static final String RACE_PATH="/api/races/{id}";
    public static final String RACE_CANCELLATION_PATH="/api/races/{id}/cancellations";

    public ResponseEntity<RaceDTO> createRace(RaceDTO newRace);
    public ResponseEntity<RaceListDTO> getRaces(Integer pageSize, Integer pageNumber);
    public ResponseEntity<RaceDTO> getRace(String id);
    public ResponseEntity<RaceDTO> updateRace(String id, RaceDTO updateRace);
    public ResponseEntity<RaceDTO> cancelRace(String id);
    public ResponseEntity<Void> deleteRace(String id);
    public ResponseEntity<Void> deleteAllRaces();
}
