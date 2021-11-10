package info.ejava_student.assignment5.db.race;

import info.ejava.assignments.api.race.client.factories.RaceDTOFactory;
import info.ejava.assignments.api.race.client.factories.RacerDTOFactory;
import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.races.RaceDTO;
import info.ejava_student.assignment5.db.race.db.registrations.RacerRegistrationBO;
import info.ejava_student.assignment5.db.race.db.registrations.RacerRegistrationMapper;
import info.ejava_student.assignment5.jpa.race.raceregistrations.JpaRacerRegistrationRepository;
import info.ejava_student.assignment5.jpa.race.raceregistrations.JpaAssignmentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes={DbRaceApp.class})
@Slf4j
@ActiveProfiles(profiles={"test"}, resolver = TestProfileResolver.class)
//@ActiveProfiles(profiles={"test", "postgres"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled("TODO: uncomment, declare dependencies, and define RacerRegistrationDTO type")
public class Jpa5c_RepositoryTest {
/*
    @Autowired
    private EntityManagerFactory emf;
*/
    @Autowired
    private RacerRegistrationMapper mapper;
    @Autowired
    private RaceDTOFactory raceDTOFactory;
    @Autowired
    private RacerDTOFactory racerDTOFactory;
    @Autowired
    private JpaAssignmentService studentAssignment;
    @Autowired
    private JpaRacerRegistrationRepository registrationRepo;
    private String commonRaceId;
    private String commonFirstName="Bob";
    private RaceDTO commonRaceDTO;
    private int minAge;
    private int maxAge;


/*
    private List<RacerRegistrationDTO> registrations = new ArrayList<>();
*/

    @Test
    void can_find_by_raceId_by_derived_query() throws SQLException {
/*
        //given
        List<RacerRegistrationDTO> matches = getOrderedMatches(r -> commonRaceId.equals(r.getRaceId()));
        Pageable pageable = PageRequest.of(3, 2, Sort.by("id").ascending());
        log.debug("min={}, max={}, pageable={}, matches=\n{}", minAge, maxAge, pageable,
                StringUtils.join(matches,System.lineSeparator()));
        Set<String> expectedRegistrationIds = getRegistrationIds(matches, pageable);

        //when
        Page<RacerRegistrationBO> page = studentAssignment.findByRaceIdByDerivedQuery(commonRaceId, pageable);

        //then
        then(page).hasSizeLessThanOrEqualTo(pageable.getPageSize());
        Set<String> foundRacerIds = page.stream().map(bo->bo.getId().toString()).collect(Collectors.toSet());
        then(foundRacerIds).isEqualTo(expectedRegistrationIds);
*/
        fail("uncomment test");
    }

    @Test
    void can_find_with_first_name_by_example() {
/*
        //given
        RacerRegistrationBO probe = RacerRegistrationBO.builder()
                .raceId(commonRaceDTO.getId())
                .firstName(commonFirstName)
                .build();
        List<RacerRegistrationDTO> matches = getOrderedMatches(
                r -> commonRaceDTO.getId().equals(r.getRaceId()) &&
                        commonFirstName.equals(r.getFirstName()));
        Pageable pageable = PageRequest.of(1, 2, Sort.by("id").ascending());
        log.debug("min={}, max={}, pageable={}, matches=\n{}", minAge, maxAge, pageable,
                StringUtils.join(matches,System.lineSeparator()));
        Set<String> expectedRegistrationIds = getRegistrationIds(matches, pageable);

        //when
        Page<RacerRegistrationBO> page = studentAssignment.findByExample(probe, pageable);

        //then
        then(page).hasSizeLessThanOrEqualTo(pageable.getPageSize());
        Set<String> foundRacerIds = page.stream().map(bo->bo.getId().toString()).collect(Collectors.toSet());
        then(foundRacerIds).isEqualTo(expectedRegistrationIds);
*/
        fail("uncomment test");
    }


    @Test
    void can_find_racers_within_age_range_by_annotated_query() {
/*
        //given
        List<RacerRegistrationDTO> matches = getOrderedMatches(
                r->r.getAge() >= minAge && r.getAge() <= maxAge);
        Pageable pageable = PageRequest.of(1, 2, Sort.by("id").ascending());
        log.debug("min={}, max={}, pageable={}, matches=\n{}", minAge, maxAge, pageable,
                StringUtils.join(matches,System.lineSeparator()));
        Set<String> expectedRegistrationIds = getRegistrationIds(matches, pageable);

        //when
        Slice<RacerRegistrationBO> slice = studentAssignment.findByAgeRangeByAnnotatedQuery(minAge, maxAge, pageable);

        //then
        then(slice).hasSizeLessThanOrEqualTo(pageable.getPageSize());
        Set<String> foundRacerIds = slice.stream().map(bo->bo.getId().toString()).collect(Collectors.toSet());
        then(foundRacerIds).isEqualTo(expectedRegistrationIds);
*/
        fail("uncomment test");
    }


    //@AfterAll
    void cleanup() {
/*
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from RacerRegistrationBO").executeUpdate();
        em.getTransaction().commit();
        assertThat(em.createQuery("select count(r) from RacerRegistrationBO r", Number.class).getSingleResult().intValue()).isZero();
        em.close();
*/
        Assertions.fail("uncomment cleanup");
    }

    @BeforeAll
    void populate() {
/*
        cleanup();
        RaceDTO raceDTO = raceDTOFactory.make(RaceDTOFactory.withId);
        commonRaceId = raceDTO.getId();
        for (int i=0; i<10; i++) {
            RacerDTO racerDTO = racerDTOFactory.make(RacerDTOFactory.withId).withUsername("xxxx");
            RacerRegistrationDTO dto = studentAssignment.makeRegistrationDTO(raceDTO, racerDTO, null);
            RacerRegistrationBO bo = mapper.map(dto);
            registrationRepo.save(bo);
            registrationRepo.flush();
            registrations.add(mapper.map(bo));
        }

        //same firstName
        for (int i=0; i<15; i++) {
            if (i%5==0) {
                raceDTO = raceDTOFactory.make(RaceDTOFactory.withId);
                commonRaceDTO = raceDTO;
            }
            RacerDTO racerDTO = racerDTOFactory.make(RacerDTOFactory.withId)
                    .withFirstName(commonFirstName)
                    .withUsername("xxxx");
            RacerRegistrationDTO dto = studentAssignment.makeRegistrationDTO(raceDTO, racerDTO, null);
            RacerRegistrationBO bo = mapper.map(dto);
            registrationRepo.save(bo);
            registrationRepo.flush();
            registrations.add(mapper.map(bo));
        }

        int min = registrations.stream().mapToInt(r->r.getAge()).min().getAsInt();
        int max = registrations.stream().mapToInt(r->r.getAge()).max().getAsInt();
        int ave = registrations.stream().mapToInt(r->r.getAge()).sum() / registrations.size();
        minAge = ave - ((ave - min) / 2);
        maxAge = ((max - ave)/ 2) + ave;
*/
        Assertions.fail("uncomment populate");
    }

/*
    private List<RacerRegistrationDTO> getOrderedMatches(Predicate<RacerRegistrationDTO> predicate){
        return registrations.stream()
                .filter(r -> predicate.test(r))
                .sorted((lhs,rhs)->lhs.getId().compareTo(rhs.getId()))
                .collect(Collectors.toList());
    }

    private Set<String> getRegistrationIds(List<RacerRegistrationDTO> matches, Pageable pageable) {
        log.debug("pageable={}", pageable);
        return matches
                .subList(pageable.getPageNumber()*pageable.getPageSize(),
                        (pageable.getPageNumber()+1)*pageable.getPageSize()).stream()
                .map(dto -> dto.getId())
                .collect(Collectors.toSet());
    }
*/
}
