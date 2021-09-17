package info.ejava.assignments.api.race.racers;

import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.racers.RacerListDTO;

public interface RacersService {
    RacerDTO createRacer(RacerDTO newRacer);
    RacerDTO getRacer(String id);
    RacerDTO updateRacer(String id, RacerDTO racerUpdate);

    RacerListDTO getRacers(Integer pageSize, Integer pageNumber);

    void deleteRacer(String id);
    void deleteAllRacers();
}
