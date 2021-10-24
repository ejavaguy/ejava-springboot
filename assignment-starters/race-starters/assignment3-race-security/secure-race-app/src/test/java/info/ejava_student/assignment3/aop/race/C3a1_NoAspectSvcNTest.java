package info.ejava_student.assignment3.aop.race;


import info.ejava.assignments.api.race.client.factories.RaceDTOFactory;
import info.ejava.assignments.api.race.client.factories.RacerDTOFactory;
import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.races.RaceDTO;
import info.ejava.assignments.api.race.racers.RacersService;
import info.ejava.assignments.api.race.races.RacesService;
import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava_student.assignment3.security.race.SecureRaceApp;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

/**
 * This test verifies baseline behavior before the Aspect is enabled.
 */
@SpringBootTest(classes={SecureRaceApp.class})
@Slf4j
@ActiveProfiles({"test","authorities", "authorization"})//no aop profile!
@DisplayName("Part C3a1: No Aspect (Service)")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Disabled("TODO")
public class C3a1_NoAspectSvcNTest {
    @Autowired
    private RaceDTOFactory raceDTOFactory;
    @Autowired
    private RacerDTOFactory racerDTOFactory;
    @Autowired
    private RacesService racesService;
    @Autowired
    private RacersService racersService;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("user1","password"));
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class no_advice_for_races {
        Stream<Arguments> races() {
            return Stream.of(
                    Arguments.of(raceDTOFactory.make(), true),
                    //without the Aspect in place, withId passes
                    Arguments.of(raceDTOFactory.make(RaceDTOFactory.withId), true)
            );
        }

        @ParameterizedTest
        @MethodSource("races")
        void given(/*given*/RaceDTO raceDTO, boolean success) {
            //when
            ClientErrorException.InvalidInputException ex = catchThrowableOfType(
                    () -> racesService.createRace(raceDTO),
                    ClientErrorException.InvalidInputException.class);
            //then
            if (success) {
                then(ex).isNull();
            } else {
                then(ex).as("error not detected by aspect").isNotNull();
                log.info("{}", ex.getMessage());
                then(ex.getMessage()).contains("id: must be null, value=");
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class no_advice_for_racers {
        Stream<Arguments> racers() {
            return Stream.of(
                    Arguments.of(racerDTOFactory.make(), true),
                    //without the Aspect in place, withId passes
                    Arguments.of(racerDTOFactory.make(RacerDTOFactory.withId), true)
            );
        }

        @ParameterizedTest
        @MethodSource("racers")
        void given(RacerDTO racerDTO, boolean success) {
            //when
            ClientErrorException.InvalidInputException ex = catchThrowableOfType(
                    () -> racersService.createRacer(racerDTO),
                    ClientErrorException.InvalidInputException.class);
            //then
            if (success) {
                then(ex).isNull();
            } else {
                then(ex).as("error not detected by aspect").isNotNull();
                log.info("{}", ex.getMessage());
                then(ex.getMessage()).contains("id: must be null, value=");
            }
        }
    }
}
