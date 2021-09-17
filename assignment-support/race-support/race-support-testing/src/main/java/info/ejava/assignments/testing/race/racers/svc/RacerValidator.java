package info.ejava.assignments.testing.race.racers.svc;

import info.ejava.assignments.testing.race.racers.dto.RacerDTO;

import java.util.List;

public interface RacerValidator {
    List<String> validateNewRacer(RacerDTO racer, int minAge);
}
