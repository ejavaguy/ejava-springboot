package info.ejava_student.assignment1.testing.races.racer.svc;

import info.ejava_student.assignment1.testing.races.racer.dto.RacerDTO;

public class RacerValidatorXxx {
    private RacerValidator subject;
    private RacerDTO validRacer;

    void init() {
    }

    void validates_valid_registration() {
        //given - a valid racer for a minAge
        //when - calling validation
        //then - racer will be reported valid
    }

    void reports_blank_name() {
        //given - an invalid racer
        //when - calling validation
        //then - racer will be reported invalid
    }
}
