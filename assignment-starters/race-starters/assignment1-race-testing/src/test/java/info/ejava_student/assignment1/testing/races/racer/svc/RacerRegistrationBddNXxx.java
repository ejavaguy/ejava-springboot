package info.ejava_student.assignment1.testing.races.racer.svc;

import info.ejava.assignments.testing.race.racers.dto.RacerDTO;
import info.ejava.assignments.testing.race.racers.svc.RacerRegistrationService;
import org.junit.jupiter.api.Test;

public class RacerRegistrationBddNXxx {
    private RacerRegistrationService subject;
    private RacerDTO validRacer;

    void can_register_valid_racer() {
        //given - registering an valid racer
        //then - racer will be validated and assigned an ID
    }

    @Test
    void rejects_invalid_racer() {
        //given - an invalid racer and a call that will eventually fail
        //when - registering the racer
        //then - racer will be validated, fail, and results reported in an exception
    }
}
