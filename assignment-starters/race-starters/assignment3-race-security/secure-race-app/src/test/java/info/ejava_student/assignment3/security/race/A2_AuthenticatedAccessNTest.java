package info.ejava_student.assignment3.security.race;

import info.ejava.assignments.api.race.client.factories.RaceDTOFactory;
import info.ejava.assignments.api.race.client.factories.RacerDTOFactory;
import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.racers.RacersAPI;
import info.ejava.assignments.api.race.client.racers.RacersAPIClient;
import info.ejava.assignments.api.race.client.races.RaceDTO;
import info.ejava.assignments.api.race.client.races.RacesAPI;
import info.ejava.assignments.api.race.client.races.RacesAPIClient;
import info.ejava.examples.common.web.ServerConfig;
import info.ejava_student.assignment3.security.race.config.RaceTestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes={SecureRaceApp.class, RaceTestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "authenticated-access"})
@Slf4j
@DisplayName("Part A2: Authenticated Access")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class A2_AuthenticatedAccessNTest {
    @Autowired
    private RestTemplate anonymousUser;
    @Autowired
    private RestTemplate authnUser;
    @Autowired
    private RestTemplate badUser;
    @Autowired
    private String authnUsername;
    @Autowired
    private String anonymousUsername;
    private URI baseUrl;
    private ServerConfig serverConfig;

    @BeforeEach
    void init(@LocalServerPort int port) {
        serverConfig = new ServerConfig().withPort(port).build();
        baseUrl = serverConfig.getBaseUrl();
    }

    @Test
    @Disabled("TODO")
    void deny_bad_password() {
        //given
        URI url = UriComponentsBuilder.fromUri(baseUrl).path("api/whoAmI").build().toUri();
        RequestEntity request = RequestEntity.get(url).build();
        //when
        RestClientResponseException ex = catchThrowableOfType(
                ()->badUser.exchange(request, String.class),
                RestClientResponseException.class);
        //then
        then(ex).isNotNull();
        then(ex.getRawStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @Disabled("TODO")
    void csrf_checking_disabled() {
        //given - a post request
        URI url = UriComponentsBuilder.fromUri(baseUrl).path("api/whoAmI").build().toUri();
        RequestEntity request = RequestEntity.post(url).build();
        //when - posting without a CSRF token
        ResponseEntity<String> response = authnUser.exchange(request, String.class);
        String body = response.getBody();
        //then
        log.info("user={}", body);
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(body).isEqualTo(authnUsername);
    }

    @Nested
    @Disabled("TODO")
    class granted_authenticated_access_to {
        @Autowired
        private RaceDTOFactory raceDTOFactory;
        @Autowired
        private RacerDTOFactory racerDTOFactory;
        private RacesAPI racesClient;
        private RacersAPI racersClient;
//        private RacerRegistrationsAPI registrationsClient;
        @BeforeEach
        void init() {
            racesClient = new RacesAPIClient(authnUser, serverConfig, MediaType.APPLICATION_JSON);
            racersClient = new RacersAPIClient(authnUser, serverConfig, MediaType.APPLICATION_JSON);
//            registrationsClient =
//                    new RacerRegistrationsAPIClient(authnUser, serverConfig, MediaType.APPLICATION_JSON);
        }

        @Test
        void races_post() {
            //given
            RaceDTO validRace = raceDTOFactory.make();
            //when
            ResponseEntity<RaceDTO> response = racesClient.createRace(validRace);
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        @Test
        @DirtiesContext
        void racers_post() {
            //given
            RacerDTO validRacer = new RacerDTOFactory().make();
            //when
            ResponseEntity<RacerDTO> response = racersClient.createRacer(validRacer);
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        @Test
        @DirtiesContext
        void registration_post() {
            //given
            RaceDTO race = racesClient.createRace(raceDTOFactory.make()).getBody();
            RacerDTO racer = racersClient.createRacer(racerDTOFactory.make()).getBody();
//            //when
//            ResponseEntity<RacerRegistrationDTO> response =
//                    registrationsClient.createRegistration(race.getId(), racer.getId());
//            //then
//            then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }
    }



    String who_am_i(RestTemplate restTemplate, HttpMethod method) {
        //given
        URI url = UriComponentsBuilder.fromUri(baseUrl).path("api/whoAmI").build().toUri();
        RequestEntity request = RequestEntity.method(method, url).build();
        //when
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        String body = response.getBody();
        //then
        log.info("user={}", body);
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return body;
    }

    @Nested
    @Disabled("TODO")
    class identify_caller_for {
        @ParameterizedTest
        @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
        void anonymous_user(HttpMethod method) {
            //when
            String identity = who_am_i(anonymousUser, method);
            //then
            then(identity).isEqualTo(anonymousUsername);
        }

        @ParameterizedTest
        @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
        void authenticated_user(HttpMethod method) {
            //when
            String identity = who_am_i(authnUser, method);
            //then
            then(identity).isEqualTo(authnUsername);
        }
    }
}
