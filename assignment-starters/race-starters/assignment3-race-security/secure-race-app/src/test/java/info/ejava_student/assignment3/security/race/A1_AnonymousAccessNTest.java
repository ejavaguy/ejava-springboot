package info.ejava_student.assignment3.security.race;

import info.ejava.assignments.api.race.client.racers.RacerListDTO;
import info.ejava.assignments.api.race.client.racers.RacersAPI;
import info.ejava.assignments.api.race.client.racers.RacersAPIClient;
import info.ejava.assignments.api.race.client.races.RaceListDTO;
import info.ejava.assignments.api.race.client.races.RacesAPI;
import info.ejava.assignments.api.race.client.races.RacesAPIClient;
import info.ejava.examples.common.web.ServerConfig;
import info.ejava_student.assignment3.security.race.config.RaceTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes= {SecureRaceApp.class, RaceTestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "anonymous-access"})
@Slf4j
@DisplayName("Part A1: Anonymous Access")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class A1_AnonymousAccessNTest {
    @Autowired
    private RestTemplate anonymousUser;
    private ServerConfig serverConfig;
    private RacesAPI racesClient;
    private RacersAPI racersClient;
//    private RacerRegistrationsAPI registrationsClient;
    private URI baseUrl;

    @BeforeEach
    void setUp(@LocalServerPort int port) {
        serverConfig = new ServerConfig().withPort(port).build();
        baseUrl = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl()).build().toUri();
        racesClient = new RacesAPIClient(anonymousUser, serverConfig, MediaType.APPLICATION_JSON);
        racersClient = new RacersAPIClient(anonymousUser, serverConfig, MediaType.APPLICATION_JSON);
//        registrationsClient = new RacerRegistrationsAPIClient(anonymousUser, serverConfig, MediaType.APPLICATION_JSON);
        log.info("baseUrl={}", baseUrl);
    }

    @Nested
    @Disabled("TODO")
    class granted_access_to {
        @Test
        void static_content() {
            //given
            URI url = UriComponentsBuilder.fromUri(baseUrl).path("content/past_results.txt").build().toUri();
            RequestEntity request = RequestEntity.get(url).accept(MediaType.TEXT_PLAIN).build();
            //when
            ResponseEntity<String> response = anonymousUser.exchange(request, String.class);
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            String body = response.getBody();
            log.info("static content={}", body);
            then(body).isNotNull();
            then(body).contains("Past Race Results");
        }

        @Test
        void races_safe_operation() {
            //when
            ResponseEntity<RaceListDTO> response = racesClient.getRaces(null, null);
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            RaceListDTO races = response.getBody();
            log.info("races={}", races);
        }

        @Test
        void racers_safe_operation() {
            //when
            ResponseEntity<RacerListDTO> response = racersClient.getRacers(null, null);
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            RacerListDTO racers = response.getBody();
            log.info("racers={}", racers);
        }

        @Test
        @Disabled("TODO")
        void registration_safe_operation() {
//            //when
//            ResponseEntity<RacerRegistrationListDTO> response = registrationsClient.getRegistrations(null, null);
//            //then
//            then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            RacerRegistrationListDTO registrations = response.getBody();
//            log.info("registrations={}", registrations);
        }
    }

    @Nested
    @Disabled("TODO")
    class denied_for_access_to {

        @Test
        void races_nonsafe_operation() {
            //when
            RestClientResponseException thrown_exception = catchThrowableOfType(
                    ()->racesClient.deleteAllRaces(),
                    RestClientResponseException.class);
            then(thrown_exception).isNotNull();
            then(thrown_exception.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        }

        @Test
        void racers_nonsafe_operation() {
            //when
            RestClientResponseException thrown_exception = catchThrowableOfType(
                    ()->racersClient.deleteAllRacers(),
                    RestClientResponseException.class);
            then(thrown_exception).isNotNull();
            then(thrown_exception.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @Disabled("TODO")
        void registration_nonsafe_operation() {
//            //when
//            RestClientResponseException thrown_exception = catchThrowableOfType(
//                    ()->registrationsClient.deleteAllRegistrations(),
//                    RestClientResponseException.class);
//            then(thrown_exception).isNotNull();
//            then(thrown_exception.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        }
    }
}
