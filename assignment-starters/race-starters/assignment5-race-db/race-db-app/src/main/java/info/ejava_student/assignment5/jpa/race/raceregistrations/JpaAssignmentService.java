package info.ejava_student.assignment5.jpa.race.raceregistrations;

import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.races.RaceDTO;
import info.ejava_student.assignment5.db.race.db.registrations.RacerRegistrationBO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface JpaAssignmentService {
/*
    RacerRegistrationDTO makeRegistrationDTO(RaceDTO race, RacerDTO racer, String id);
    RacerRegistrationBO makeRegistrationBO(RaceDTO race, RacerDTO racer, String id);

    List<RacerRegistrationDTO> mapPersistQuery(RacerRegistrationDTO requestDTO);
*/
    Page<RacerRegistrationBO> findByRaceIdByDerivedQuery(String raceId, Pageable pageable);
    Page<RacerRegistrationBO> findByExample(RacerRegistrationBO probe, Pageable pageable);
    Slice<RacerRegistrationBO> findByAgeRangeByAnnotatedQuery(int min, int max, Pageable pageable);
}
