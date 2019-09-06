package info.ejava_student.assignment1.logging.race.svc;

import info.ejava_student.assignment1.logging.race.bo.RaceResult;

import java.time.Duration;

public interface RaceResultsHelper {
    Duration calcLead(RaceResult leader, RaceResult targetResult);
}
