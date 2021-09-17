package info.ejava.assignments.api.race.client.racers;

import org.springframework.http.ResponseEntity;

public interface RacersAPI {
    public static final String RACERS_PATH="/api/racers";
    public static final String RACER_PATH="/api/racers/{id}";

    public ResponseEntity<RacerDTO> createRacer(RacerDTO newRace);
    public ResponseEntity<RacerListDTO> getRacers(Integer pageSize, Integer pageNumber);
    public ResponseEntity<RacerDTO> getRacer(String id);
    public ResponseEntity<RacerDTO> updateRacer(String id, RacerDTO updateRace);
    public ResponseEntity<Void> deleteRacer(String id);
    public ResponseEntity<Void> deleteAllRacers();
}
