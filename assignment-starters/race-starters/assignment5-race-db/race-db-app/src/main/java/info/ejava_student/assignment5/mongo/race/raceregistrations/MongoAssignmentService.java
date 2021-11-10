package info.ejava_student.assignment5.mongo.race.raceregistrations;

import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.races.RaceDTO;
import info.ejava_student.assignment5.db.race.db.registrations.RacerRegistrationBO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface MongoAssignmentService {
    RacerRegistrationBO makeRegistrationBO(RaceDTO race, RacerDTO racer, String id);
    /*
    RacerRegistrationDTO makeRegistrationDTO(RaceDTO race, RacerDTO racer, String id);

    List<RacerRegistrationDTO> mapPersistQuery(RacerRegistrationDTO requestDTO);
    */
    Page<RacerRegistrationBO> findByRaceIdByDerivedQuery(String commonRaceId, Pageable pageable);
    Page<RacerRegistrationBO> findByExample(RacerRegistrationBO probe, Pageable pageable);
    Slice<RacerRegistrationBO> findByAgeRangeByAnnotatedQuery(int minAge, int maxAge, Pageable pageable);
}
