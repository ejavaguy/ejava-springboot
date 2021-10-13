package info.ejava_student.assignment3.security.race;

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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes = {SecureRaceApp.class,
        AuthorizationTestHelperConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test","userdetails"})
@Slf4j
@DisplayName("Part A3: UserDetails")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class A3_UserDetailsNTest {
    @Autowired
    @Qualifier("userMap")
    private Map<String, RestTemplate> authnUsers;
    @Autowired
    private RestTemplate anonymousUser;
    @Autowired
    RaceDTOFactory raceDTOFactory;
    @Autowired
    RacerDTOFactory racerDTOFactory;
    private ServerConfig serverConfig;
    private URI whoAmIUrl;

    @BeforeEach
    void init(@LocalServerPort int port) {
        serverConfig = new ServerConfig().withPort(port).build();
        whoAmIUrl = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path("api/whoAmI")
                .build().toUri();
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Disabled("TODO")
    class with_identities {
        @ParameterizedTest
        @MethodSource("identities")
        void valid_credentials_can_authenticate(String username, RestTemplate restTemplate) {
            //given
            RequestEntity request = RequestEntity.get(whoAmIUrl).build();
            //when
            ResponseEntity<String> response = restTemplate.exchange(request, String.class);
            String body = response.getBody();
            //then
            log.info("user={}", body);
            then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            then(body).isEqualTo(username);
        }

        @ParameterizedTest
        @MethodSource("identities")
        void valid_credentials_can_create_race(String username, RestTemplate restTemplate) {
            //given
            RaceDTO validRace = raceDTOFactory.make();
            RacesAPI racesClient = new RacesAPIClient(restTemplate, serverConfig, MediaType.APPLICATION_JSON);
            //when
            ResponseEntity<RaceDTO> response = racesClient.createRace(validRace);
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        Stream<Arguments> identities() {
            return authnUsers.entrySet().stream()
                    .map(au->Arguments.of(au.getKey(), au.getValue()));
        }
    }

    @Nested
    @DirtiesContext
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Disabled("TODO")
    class create_racer {
        @ParameterizedTest
        @MethodSource("identities")
        void valid_credentials_can_create_racer(String username, RestTemplate restTemplate) {
            //given
            RacerDTO validRacer = racerDTOFactory.make();
            RacersAPI racersClient = new RacersAPIClient(restTemplate, serverConfig, MediaType.APPLICATION_JSON);
            //when
            ResponseEntity<RacerDTO> response = racersClient.createRacer(validRacer);
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        Stream<Arguments> identities() {
            return authnUsers.entrySet().stream()
                    .map(au->Arguments.of(au.getKey(), au.getValue()));
        }
    }

    @Nested
    @DirtiesContext
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Disabled("TODO")
    class create_registration {
        @ParameterizedTest
        @MethodSource("identities")
        void valid_credentials_can_create_registration(String username, RestTemplate restTemplate) {
            //given
            RacesAPI racesClient = new RacesAPIClient(restTemplate, serverConfig, MediaType.APPLICATION_JSON);
            RacersAPI racersClient = new RacersAPIClient(restTemplate, serverConfig, MediaType.APPLICATION_JSON);
//            RacerRegistrationsAPI registrationClient =
//                    new RacerRegistrationsAPIClient(restTemplate, serverConfig, MediaType.APPLICATION_JSON);
//            RaceDTO validRace = racesClient.createRace(raceDTOFactory.make()).getBody();
//            RacerDTO validRacer = racersClient.createRacer(racerDTOFactory.make()).getBody();
//            //when
//            ResponseEntity<RacerRegistrationDTO> response =
//                    registrationClient.createRegistration(validRace.getId(), validRacer.getId());
//            //then
//            then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        Stream<Arguments> identities() {
            return authnUsers.entrySet().stream()
                    .map(au->Arguments.of(au.getKey(), au.getValue()));
        }
    }

}
