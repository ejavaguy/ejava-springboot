package info.ejava_student.assignment3.aop.race;


import info.ejava.assignments.api.race.client.factories.RaceDTOFactory;
import info.ejava.assignments.api.race.client.factories.RacerDTOFactory;
import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.racers.RacersAPI;
import info.ejava.assignments.api.race.client.racers.RacersAPIClient;
import info.ejava.assignments.api.race.client.races.RaceDTO;
import info.ejava.assignments.api.race.client.races.RacesAPI;
import info.ejava.assignments.api.race.client.races.RacesAPIClient;
import info.ejava.assignments.security.race.config.AuthorizationTestHelperConfiguration;
import info.ejava.examples.common.web.ServerConfig;
import info.ejava_student.assignment3.security.race.SecureRaceApp;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

/**
 * This test mostly demonstrates that we have the desired behavior changes
 * to the RacesService and RacersService in what is injected into the
 * Races/Racers controllers. If the previous service test works, this should
 * work as well as long as there are no security issues.
 */
@SpringBootTest(classes={SecureRaceApp.class,
        AuthorizationTestHelperConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@ActiveProfiles({"test","authorities", "authorization", "aop"})
@DisplayName("Part C3b: Aspect (Web)")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Disabled("TODO")
public class C3b_AspectWebNTest {
    @Autowired
    private RaceDTOFactory raceDTOFactory;
    @Autowired
    private RacerDTOFactory racerDTOFactory;
    @Autowired
    private RestTemplate authnUser;
    private RacesAPI racesService;
    private RacersAPI racersService;

    @BeforeAll
    void init(@LocalServerPort int port) {
        ServerConfig serverConfig = new ServerConfig().withPort(port).build();
        racesService = new RacesAPIClient(authnUser, serverConfig, MediaType.APPLICATION_JSON);
        racersService = new RacersAPIClient(authnUser, serverConfig, MediaType.APPLICATION_JSON);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class can_advise_races {
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
            HttpClientErrorException.UnprocessableEntity ex = catchThrowableOfType(
                    () -> racesService.createRace(raceDTO),
                    HttpClientErrorException.UnprocessableEntity.class);
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
    class can_advise_racers {
        Stream<Arguments> racers() {
            return Stream.of(
                    Arguments.of(racerDTOFactory.make(), true),
                    Arguments.of(racerDTOFactory.make(RacerDTOFactory.withId), false)
            );
        }
        @ParameterizedTest
        @MethodSource("racers")
        void given(RacerDTO racerDTO,boolean success){
            //when
            HttpClientErrorException.UnprocessableEntity ex = catchThrowableOfType(
                    () -> racersService.createRacer(racerDTO),
                    HttpClientErrorException.UnprocessableEntity.class);
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
