package info.ejava.assignments.security.race.races;

import info.ejava.assignments.api.race.client.factories.RaceDTOFactory;
import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.races.RaceDTO;
import info.ejava.assignments.api.race.client.races.RaceListDTO;
import info.ejava.assignments.api.race.client.races.RacesAPI;
import info.ejava.assignments.api.race.client.races.RacesAPIClient;
import info.ejava.assignments.security.race.config.AuthoritiesConfiguration;
import info.ejava.examples.common.web.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.BDDAssertions;
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
public class RacesAuthorizationNTest {
    @Autowired
    private RaceDTOFactory raceDTOFactory;
    private RacesAPI adminClient;
    private ServerConfig serverConfig;

    @BeforeEach
    void init(@LocalServerPort int port, @Autowired RestTemplate adminUser) {
        serverConfig = new ServerConfig().withPort(port).build();
        adminClient = new RacesAPIClient(adminUser, serverConfig, MediaType.APPLICATION_JSON);
        //might fail if not correctly implemented when test first authored
        catchThrowable(()->adminClient.deleteAllRaces());
    }

    @Nested
    class anonymous_user {
        private RacesAPI anonymousClient;
        @BeforeEach
        void init(@Autowired RestTemplate anonymousUser) {
            anonymousClient = new RacesAPIClient(anonymousUser, serverConfig, MediaType.APPLICATION_JSON);
        }

        @Test
        void can_get_race() {
            //when
            RestClientResponseException ex = Assertions.assertThrows(
                    RestClientResponseException.class,
                    () -> anonymousClient.getRace("aRaceId"));
            //then - was not rejected because of identity
            then(ex.getRawStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        @Test
        void can_get_races() {
            //when
            ResponseEntity<RaceListDTO> response = anonymousClient.getRaces(1,0);
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    class authenticated_user {
        private RacesAPI authnClient;
        private RacesAPI altClient;
        @BeforeEach
        void init(@Autowired RestTemplate authnUser, @Autowired RestTemplate altUser) {
            authnClient = new RacesAPIClient(authnUser, serverConfig, MediaType.APPLICATION_JSON);
            altClient = new RacesAPIClient(altUser, serverConfig, MediaType.APPLICATION_JSON);
        }

        @Test
        void can_create_race() {
            //given
            RaceDTO validRace = raceDTOFactory.make();
            //when
            ResponseEntity<RaceDTO> response = authnClient.createRace(validRace);
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            RaceDTO createdRace = response.getBody();
            then(createdRace.getOwnername()).as("ownername not redacted").isNull();
        }
        @Test
        void cannot_modify_another_owners_race() {
            //given
            RaceDTO existingRace = authnClient.createRace(raceDTOFactory.make()).getBody();
            RaceDTO modifiedRace = existingRace.withName(existingRace.getName() + " modified");
            //when
            RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
                    () -> altClient.updateRace(existingRace.getId(), modifiedRace));
            //then
            then(ex.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        }
        @Test
        void cannot_cancel_another_owners_race() {
            //given
            RaceDTO existingRace = authnClient.createRace(raceDTOFactory.make()).getBody();
            RaceDTO modifiedRace = existingRace.withName(existingRace.getName() + " modified");
            //when
            RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
                    () -> altClient.cancelRace(existingRace.getId()));
            //then
            then(ex.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        }
        @Test
        void cannot_delete_another_owners_race() {
            //given
            RaceDTO existingRace = authnClient.createRace(raceDTOFactory.make()).getBody();
            //when
            RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
                    () -> altClient.deleteRace(existingRace.getId()));
            //then
            then(ex.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        }
    }

    @Nested
    class race_owner {
        private RacesAPI authnClient;
        @BeforeEach
        void init(@Autowired RestTemplate authnUser) {
            authnClient = new RacesAPIClient(authnUser, serverConfig, MediaType.APPLICATION_JSON);
        }

        @Test
        void can_modify_their_race() {
            //given
            RaceDTO existingRace = authnClient.createRace(raceDTOFactory.make()).getBody();
            RaceDTO modifiedRace = existingRace.withName(existingRace.getName() + " modified");
            //when
            ResponseEntity<RaceDTO> response = authnClient.updateRace(existingRace.getId(), modifiedRace);
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
        @Test
        void can_cancel_their_race() {
            //given
            RaceDTO existingRace = authnClient.createRace(raceDTOFactory.make()).getBody();
            //when
            ResponseEntity<RaceDTO> response = authnClient.cancelRace(existingRace.getId());
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
        @Test
        void can_delete_their_race() {
            //given
            RaceDTO existingRace = authnClient.createRace(raceDTOFactory.make()).getBody();
            //when
            ResponseEntity response = authnClient.deleteRace(existingRace.getId());
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class user_having_role_mgr {
        private RacesAPI authnClient;
        private RacesAPI mgrClient;
        @BeforeEach
        void init(@Autowired RestTemplate mgrUser, @Autowired RestTemplate authnUser) {
            mgrClient = new RacesAPIClient(mgrUser, serverConfig, MediaType.APPLICATION_JSON);
            authnClient = new RacesAPIClient(authnUser, serverConfig, MediaType.APPLICATION_JSON);
        }

        @Test
        void can_delete_any_race() {
            //given
            RaceDTO existingRace = authnClient.createRace(raceDTOFactory.make()).getBody();
            //when
            ResponseEntity response = mgrClient.deleteRace(existingRace.getId());
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
        @Test
        void cannot_delete_all_races() {
            //given
            RaceDTO existingRace = authnClient.createRace(raceDTOFactory.make()).getBody();
            //when
            RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
                    ()->mgrClient.deleteAllRaces());
            //then
            then(ex.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        }
    }

    @Nested
    class user_having_role_admin {
        @Test
        void admin_can_delete_all_races() {
            //when
            ResponseEntity response = adminClient.deleteAllRaces();
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }
}
