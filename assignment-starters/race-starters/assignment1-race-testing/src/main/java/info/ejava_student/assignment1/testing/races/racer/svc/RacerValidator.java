package info.ejava_student.assignment1.testing.races.racer.svc;

import info.ejava_student.assignment1.testing.races.racer.dto.RacerDTO;

import java.util.List;

public interface RacerValidator {
    List<String> validateNewRacer(RacerDTO racer, int minAge);
}
