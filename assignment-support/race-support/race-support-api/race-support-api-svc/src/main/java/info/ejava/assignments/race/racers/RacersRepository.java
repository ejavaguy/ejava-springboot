package info.ejava.assignments.race.racers;

import info.ejava.assignments.race.client.racers.RacerDTO;

import java.util.List;
import java.util.Optional;

public interface RacersRepository {
    RacerDTO save(RacerDTO racer);
    boolean existsById(String id);
    Optional<RacerDTO> findById(String id);
    List<RacerDTO> findAll(int pageSize, int pageNumber);
    void delete(RacerDTO racer);
    void deleteAll();
}
