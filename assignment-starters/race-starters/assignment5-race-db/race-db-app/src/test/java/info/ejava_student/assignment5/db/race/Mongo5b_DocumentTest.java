package info.ejava_student.assignment5.db.race;

import info.ejava.assignments.api.race.client.factories.RaceDTOFactory;
import info.ejava.assignments.api.race.client.factories.RacerDTOFactory;
import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.races.RaceDTO;
import info.ejava_student.assignment5.db.race.db.registrations.RacerRegistrationBO;
import info.ejava_student.assignment5.db.race.db.registrations.RacerRegistrationMapper;
import info.ejava_student.assignment5.mongo.race.raceregistrations.MongoAssignmentService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Period;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes={DbRaceApp.class})
@ActiveProfiles(profiles = "test", resolver = TestProfileResolver.class)
//@ActiveProfiles(profiles = {"test", "mongodb"}, resolver = TestProfileResolver.class)
@Disabled("TODO: uncomment, declare dependencies, and define RacerRegistrationDTO type")
public class Mongo5b_DocumentTest {
    @Autowired
    private RacerRegistrationMapper mapper;
    @Autowired
    private MongoAssignmentService assignmentService;
    @Autowired
    private RaceDTOFactory raceDTOFactory;
    @Autowired
    private RacerDTOFactory racerDTOFactory;


    @Test
    void map_dto_to_bo() {
/*
        //given
        RaceDTO race = raceDTOFactory.make(RaceDTOFactory.withId);
        RacerDTO racer = racerDTOFactory.make(RacerDTOFactory.withId).withUsername("xxxx");
        RacerRegistrationDTO dto = assignmentService.makeRegistrationDTO(race, racer, ObjectId.get().toString());

        //when
        RacerRegistrationBO bo = mapper.map(dto);

        //then
        then(bo.getId()).isEqualTo(dto.getId());
        then(bo.getRaceId()).isEqualTo(dto.getRaceId());
        then(bo.getRaceName()).isEqualTo(dto.getRaceName());
        then(bo.getRaceDate()).isEqualTo(dto.getRaceDate());

        then(bo.getRacerId()).isEqualTo(dto.getRacerId());
        then(bo.getFirstName()).isEqualTo(dto.getFirstName());
        then(bo.getLastName()).isEqualTo(dto.getLastName());
        then(bo.getGender()).isEqualTo(dto.getGender());
        then(bo.getAge()).isEqualTo(dto.getAge());

        then(bo.getOwnername()).isEqualTo(dto.getOwnername());
*/
        fail("uncomment test");
    }

    @Test
    void map_bo_to_dto() {
/*
        //given
        RaceDTO race = raceDTOFactory.make(RaceDTOFactory.withId);
        RacerDTO racer = racerDTOFactory.make(RacerDTOFactory.withId).withUsername("xxxxx");
        RacerRegistrationBO bo = RacerRegistrationBO.builder()
                .id(ObjectId.get().toHexString())
                .ownername("xxxx")
                .raceId(race.getId())
                .raceName(race.getName())
                .raceDate(race.getEventDate())
                .racerId(racer.getId())
                .firstName(racer.getFirstName())
                .lastName(racer.getLastName())
                .age(Period.between(race.getEventDate(), racer.getDob()).getYears())
                .build();

        //when
        RacerRegistrationDTO dto = mapper.map(bo);

        //then
        then(dto.getId()).isEqualTo(bo.getId());
        then(dto.getRaceId()).isEqualTo(bo.getRaceId());
        then(dto.getRaceName()).isEqualTo(bo.getRaceName());
        then(dto.getRaceDate()).isEqualTo(bo.getRaceDate());

        then(dto.getRacerId()).isEqualTo(bo.getRacerId());
        then(dto.getFirstName()).isEqualTo(bo.getFirstName());
        then(dto.getLastName()).isEqualTo(bo.getLastName());
        then(dto.getGender()).isEqualTo(bo.getGender());
        then(dto.getAge()).isEqualTo(bo.getAge());

        then(bo.getOwnername()).isEqualTo(dto.getOwnername());
*/
        fail("uncomment test");
    }

    @Test
    void map_persist_query() {
/*
        //given
        RaceDTO race = raceDTOFactory.make(RaceDTOFactory.withId);
        RacerDTO racer = racerDTOFactory.make(RacerDTOFactory.withId).withUsername("xxxx");
        RacerRegistrationDTO requestDTO = assignmentService.makeRegistrationDTO(race, racer, null);

        //when
        List<RacerRegistrationDTO> results = assignmentService.mapPersistQuery(requestDTO);

        //then
        Optional<RacerRegistrationDTO> result = results.stream()
                .filter(r -> race.getId().equals(r.getRaceId()))
                .filter(r -> racer.getId().equals(r.getRacerId()))
                .findFirst();
        then(result).as("intended entity no returned").isPresent();
*/
        fail("uncomment test");
    }

}
