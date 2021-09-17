package info.ejava.assignments.api.race.races;

import info.ejava.assignments.api.race.client.races.RaceDTO;

import java.util.List;
import java.util.Optional;

public interface RacesRepository {
    RaceDTO save(RaceDTO race);
    boolean existsById(String id);
    Optional<RaceDTO> findById(String id);
    List<RaceDTO> findAll(int pageSize, int pageNumber);
    void delete(RaceDTO race);
    void deleteAll();
}
