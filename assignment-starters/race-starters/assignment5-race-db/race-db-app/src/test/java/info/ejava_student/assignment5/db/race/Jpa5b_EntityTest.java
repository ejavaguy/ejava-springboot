package info.ejava_student.assignment5.db.race;

import info.ejava.assignments.api.race.client.factories.RaceDTOFactory;
import info.ejava.assignments.api.race.client.factories.RacerDTOFactory;
import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.races.RaceDTO;
import info.ejava_student.assignment5.db.race.db.registrations.RacerRegistrationBO;
import info.ejava_student.assignment5.jpa.race.raceregistrations.JpaAssignmentService;
import info.ejava_student.assignment5.db.race.db.registrations.RacerRegistrationMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes={DbRaceApp.class})
@Slf4j
@ActiveProfiles(profiles={"test"}, resolver = TestProfileResolver.class)
//@ActiveProfiles(profiles={"test", "postgres"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled("TODO: uncomment, declare dependencies, and define RacerRegistrationDTO type")
public class Jpa5b_EntityTest {
/*
    @Autowired
    EntityManagerFactory emf;
*/
    @Autowired
    RacerRegistrationMapper mapper;
    @Autowired
    RaceDTOFactory raceDTOFactory;
    @Autowired
    RacerDTOFactory racerDTOFactory;
    @Autowired
    JpaAssignmentService assignmentService;

    @BeforeAll
    @AfterAll
    void cleanup() {
/*
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from RacerRegistrationBO").executeUpdate();
        em.getTransaction().commit();
        em.close();
*/
    }

    @Test
    void map_dto_to_bo() {
/*
        //given
        RaceDTO race = raceDTOFactory.make(RaceDTOFactory.withId);
        RacerDTO racer = racerDTOFactory.make(RacerDTOFactory.withId).withUsername("xxxx");
        RacerRegistrationDTO dto = assignmentService.makeRegistrationDTO(race, racer, "123");

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
                .id("123")
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
    void map_persist_query() throws SQLException {
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
