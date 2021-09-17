package info.ejava.assignments.api.race.races;

import info.ejava.assignments.api.race.client.races.RaceDTO;
import info.ejava.assignments.api.race.client.races.RaceListDTO;

public interface RacesService {
    RaceDTO createRace(RaceDTO newRace);
    RaceDTO getRace(String id);
    RaceDTO updateRace(String id, RaceDTO updateRace);

    RaceDTO cancelRace(String id);

    RaceListDTO getRaces(Integer pageSize, Integer pageNumber);

    void deleteRace(String id);
    void deleteAllRaces();
}
