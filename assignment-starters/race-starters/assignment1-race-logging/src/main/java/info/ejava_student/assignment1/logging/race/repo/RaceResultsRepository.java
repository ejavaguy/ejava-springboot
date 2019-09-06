package info.ejava_student.assignment1.logging.race.repo;

import info.ejava_student.assignment1.logging.race.bo.RaceResult;

public interface RaceResultsRepository {
    RaceResult getLeaderByRaceId(String raceId);
    RaceResult getResultByRacerId(String racerId);
}
