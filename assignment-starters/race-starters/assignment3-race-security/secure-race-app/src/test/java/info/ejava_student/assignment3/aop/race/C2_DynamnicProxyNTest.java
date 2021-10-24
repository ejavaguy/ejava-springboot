package info.ejava_student.assignment3.aop.race;


import info.ejava.assignments.aop.race.NullPropertyAssertion;
import info.ejava.assignments.api.race.client.factories.RaceDTOFactory;
import info.ejava.assignments.api.race.client.factories.RacerDTOFactory;
import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.races.RaceDTO;
import info.ejava.assignments.api.race.racers.RacersService;
import info.ejava.assignments.api.race.races.RacesService;
import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava_student.assignment3.security.race.SecureRaceApp;
import info.ejava_student.assignment3.security.race.config.RaceTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes={SecureRaceApp.class, RaceTestConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@DisplayName("Part C2: Dynamic Proxies")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Disabled("TODO")
public class C2_DynamnicProxyNTest {
    @Autowired
    private RaceDTOFactory raceDTOFactory;
    @Autowired
    private RacerDTOFactory racerDTOFactory;
    @Autowired
    private NullPropertyAssertion nullPropertyAssertion;
    @Autowired
    private RacesService racesService;
    @Autowired
    private RacersService racersService;
    private RacesService racesProxy;
    private RacersService racersProxy;

    @BeforeAll
    void init() {
        racesProxy = NullValidatorHandler.newInstance(
                racesService,
                List.of("createRace"),
                nullPropertyAssertion,
                List.of("id"));
        racersProxy = NullValidatorHandler.newInstance(
                racersService,
                List.of("createRacer"),
                nullPropertyAssertion,
                List.of("id"));

        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("user1","password"));
    }


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class can_proxy_races {
        Stream<Arguments> races() {
            return Stream.of(
                    Arguments.of(raceDTOFactory.make(), true),
                    Arguments.of(raceDTOFactory.make(RaceDTOFactory.withId), false)
            );
        }

        @ParameterizedTest
        @MethodSource("races")
        void given(RaceDTO raceDTO, boolean success) {
            //when
            ClientErrorException.InvalidInputException ex = catchThrowableOfType(
                    () -> racesProxy.createRace(raceDTO),
                    ClientErrorException.InvalidInputException.class);
            //then
            if (success) {
                then(ex).isNull();
            } else {
                then(ex).as("error not detected by dynamic proxy").isNotNull();
                log.info("{}", ex.getMessage());
                then(ex.getMessage()).contains("id: must be null, value=");
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class can_proxy_racers {
        Stream<Arguments> racers() {
            return Stream.of(
                    Arguments.of(racerDTOFactory.make(), true),
                    Arguments.of(racerDTOFactory.make(RacerDTOFactory.withId), false)
            );
        }

        @ParameterizedTest
        @MethodSource("racers")
        void given(RacerDTO racerDTO, boolean success) {
            //when
            ClientErrorException.InvalidInputException ex = catchThrowableOfType(
                    () -> racersProxy.createRacer(racerDTO),
                    ClientErrorException.InvalidInputException.class);
            //then
            if (success) {
                then(ex).isNull();
            } else {
                then(ex).as("error not detected by dynamic proxy").isNotNull();
                log.info("{}", ex.getMessage());
                then(ex.getMessage()).contains("id: must be null, value=");
            }
        }
    }
}
