package info.ejava_student.assignment1.logging.race.svc;

import java.time.Duration;

public interface RaceResultsService {
    Duration calcLead(String raceId, String racerId);
}
