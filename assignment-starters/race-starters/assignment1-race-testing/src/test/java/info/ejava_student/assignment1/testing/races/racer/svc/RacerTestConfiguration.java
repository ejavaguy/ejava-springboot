package info.ejava_student.assignment1.testing.races.racer.svc;

import info.ejava_student.assignment1.testing.races.racer.dto.RacerDTO;

import java.time.LocalDate;

public class RacerTestConfiguration {
    public RacerDTO validRacer() {
        return RacerDTO.builder()
                .firstName("manny")
                .lastName("pep")
                .dob(LocalDate.ofYearDay(1923,1))
                .build();
    }
}
