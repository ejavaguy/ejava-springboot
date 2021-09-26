package info.ejava.assignments.security.race.racers;

import info.ejava.assignments.api.race.client.factories.RacerDTOFactory;
import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.racers.RacerListDTO;
import info.ejava.assignments.api.race.client.racers.RacersAPI;
import info.ejava.assignments.api.race.client.racers.RacersAPIClient;
import info.ejava.assignments.security.race.config.AuthoritiesConfiguration;
import info.ejava.examples.common.web.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.BDDAssertions;
import org.assertj.core.api.BDDSoftAssertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes={AuthoritiesConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "debug=true")
@Slf4j
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ActiveProfiles("authorities")
public class RacersAuthorizationNTest {
    @Autowired
    private RacerDTOFactory racerDTOFactory;
    private ServerConfig serverConfig;
    private RacersAPI adminClient;

    @BeforeEach
    void init(@LocalServerPort int port, @Autowired RestTemplate adminUser) {
        serverConfig = new ServerConfig().withPort(port).build();
        adminClient = new RacersAPIClient(adminUser, serverConfig, MediaType.APPLICATION_JSON);
        //might fail if not correctly implemented when test first authored
        catchThrowable(()->adminClient.deleteAllRacers());
    }

    @Nested
    class anonymous_user {
        private RacersAPI anonymousClient;
        @BeforeEach
        void init(@Autowired RestTemplate anonymousUser) {
            anonymousClient = new RacersAPIClient(anonymousUser, serverConfig, MediaType.APPLICATION_JSON);
        }

        @Test
        void can_get_racer() {
            //when
            RestClientResponseException ex = Assertions.assertThrows(
                    RestClientResponseException.class,
                    () -> anonymousClient.getRacer("aRacerId"));
            //then - was not rejected because of identity
            then(ex.getRawStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        @Test
        void can_get_racers() {
            //when
            ResponseEntity<RacerListDTO> response = anonymousClient.getRacers(1,0);
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    class authenticated_user {
        private RacersAPI authnClient;
        private RacersAPI altClient;
        @BeforeEach
        void init(@Autowired RestTemplate authnUser, @Autowired RestTemplate altUser) {
            authnClient = new RacersAPIClient(authnUser, serverConfig, MediaType.APPLICATION_JSON);
            altClient = new RacersAPIClient(altUser, serverConfig, MediaType.APPLICATION_JSON);
        }

        @Test
        void can_create_race() {
            //given
            RacerDTO validRacer = racerDTOFactory.make();
            //when
            ResponseEntity<RacerDTO> response = authnClient.createRacer(validRacer);
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            RacerDTO createdRacer = response.getBody();
            then(createdRacer.getUsername()).as("username not redacted").isNull();
        }
        @Test
        void cannot_modify_another_owners_race() {
            //given
            RacerDTO existingRacer = authnClient.createRacer(racerDTOFactory.make()).getBody();
            RacerDTO modifiedRacer = existingRacer.withFirstName(existingRacer.getFirstName() + " modified");
            //when
            RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
                    () -> altClient.updateRacer(existingRacer.getId(), modifiedRacer));
            //then
            then(ex.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        }
        @Test
        void cannot_delete_another_owners_race() {
            //given
            RacerDTO existingRacer = authnClient.createRacer(racerDTOFactory.make()).getBody();
            //when
            RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
                    () -> altClient.deleteRacer(existingRacer.getId()));
            //then
            then(ex.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        }
    }

    @Nested
    class race_owner {
        private RacersAPI authnClient;
        @BeforeEach
        void init(@Autowired RestTemplate authnUser) {
            authnClient = new RacersAPIClient(authnUser, serverConfig, MediaType.APPLICATION_JSON);
        }

        @Test
        void can_modify_their_race() {
            //given
            RacerDTO existingRacer = authnClient.createRacer(racerDTOFactory.make()).getBody();
            RacerDTO modifiedRacer = existingRacer.withFirstName(existingRacer.getFirstName() + " modified");
            //when
            ResponseEntity<RacerDTO> response = authnClient.updateRacer(existingRacer.getId(), modifiedRacer);
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
        @Test
        void can_delete_their_race() {
            //given
            RacerDTO existingRacer = authnClient.createRacer(racerDTOFactory.make()).getBody();
            //when
            ResponseEntity response = authnClient.deleteRacer(existingRacer.getId());
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class user_having_role_mgr {
        private RacersAPI authnClient;
        private RacersAPI mgrClient;
        @BeforeEach
        void init(@Autowired RestTemplate mgrUser, @Autowired RestTemplate authnUser) {
            mgrClient = new RacersAPIClient(mgrUser, serverConfig, MediaType.APPLICATION_JSON);
            authnClient = new RacersAPIClient(authnUser, serverConfig, MediaType.APPLICATION_JSON);
        }

        @Test
        void can_delete_any_racer() {
            //given
            RacerDTO existingRacer = authnClient.createRacer(racerDTOFactory.make()).getBody();
            //when
            ResponseEntity response = mgrClient.deleteRacer(existingRacer.getId());
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
        @Test
        void cannot_delete_all_racers() {
            //given
            RacerDTO existingRacer = authnClient.createRacer(racerDTOFactory.make()).getBody();
            //when
            RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
                    ()->mgrClient.deleteAllRacers());
            //then
            then(ex.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        }
    }

    @Nested
    class user_having_role_admin {
        @Test
        void admin_can_delete_all_racers() {
            //when
            ResponseEntity response = adminClient.deleteAllRacers();
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }
}
