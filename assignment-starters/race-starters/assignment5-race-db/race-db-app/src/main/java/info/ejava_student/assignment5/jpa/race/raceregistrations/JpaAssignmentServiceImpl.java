package info.ejava_student.assignment5.jpa.race.raceregistrations;

import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.races.RaceDTO;
import info.ejava_student.assignment5.db.race.db.registrations.RacerRegistrationBO;
import info.ejava_student.assignment5.db.race.db.registrations.RacerRegistrationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;

import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class JpaAssignmentServiceImpl implements JpaAssignmentService {
    private final RacerRegistrationMapper mapper;
//    private final EntityManager entityManager;
    private final JpaRacerRegistrationRepository repo;
/*
    private static RacerRegistrationDTOFactory dtoFactory=new RacerRegistrationDTOFactory();

    @Override
    public RacerRegistrationDTO makeRegistrationDTO(RaceDTO race, RacerDTO racer, String id) {
        return dtoFactory.make(race, racer).withId(id);
    }

    @Override
    public RacerRegistrationBO makeRegistrationBO(RaceDTO race, RacerDTO racer, String id) {
        return RacerRegistrationBO.builder()
                .id(id)
                ...
                .build();
    }

    public List<RacerRegistrationDTO> mapPersistQuery(RacerRegistrationDTO requestDTO) {
        //map DTO to BO
        //persist BO
        //query for BOs matching named query
        //map BOs to DTOs
        //return DTOs
    }
*/
    @Override
    public Page<RacerRegistrationBO> findByRaceIdByDerivedQuery(String raceId, Pageable pageable) {
        return new PageImpl<>(Collections.emptyList(), pageable, 0L); //TODO
    }

    @Override
    public Page<RacerRegistrationBO> findByExample(RacerRegistrationBO probe, Pageable pageable) {
        return new PageImpl<>(Collections.emptyList(), pageable, 0L); //TODO
    }

    @Override
    public Slice<RacerRegistrationBO> findByAgeRangeByAnnotatedQuery(int min, int max, Pageable pageable) {
        return new PageImpl<>(Collections.emptyList(), pageable, 0L); //TODO
    }

}
