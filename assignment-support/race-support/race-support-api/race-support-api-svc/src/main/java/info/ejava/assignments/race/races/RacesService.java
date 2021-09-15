package info.ejava.assignments.race.races;

import info.ejava.assignments.race.client.races.RaceDTO;
import info.ejava.assignments.race.client.races.RaceListDTO;

public interface RacesService {
    RaceDTO createRace(RaceDTO newRace);
    RaceDTO getRace(String id);
    RaceDTO updateRace(String id, RaceDTO updateRace);

    RaceDTO cancelRace(String id);

    RaceListDTO getRaces(Integer pageSize, Integer pageNumber);

    void deleteRace(String id);
    void deleteAllRaces();
}
