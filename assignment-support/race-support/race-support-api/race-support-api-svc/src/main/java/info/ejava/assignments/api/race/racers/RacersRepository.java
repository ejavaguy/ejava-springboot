package info.ejava.assignments.api.race.racers;

import info.ejava.assignments.api.race.client.racers.RacerDTO;

import java.util.List;
import java.util.Optional;

public interface RacersRepository {
    RacerDTO save(RacerDTO racer);
    boolean existsById(String id);
    Optional<RacerDTO> findById(String id);
    Optional<RacerDTO> findByUsername(String username);
    List<RacerDTO> findAll(int pageSize, int pageNumber);
    void delete(RacerDTO racer);
    void deleteAll();
}
