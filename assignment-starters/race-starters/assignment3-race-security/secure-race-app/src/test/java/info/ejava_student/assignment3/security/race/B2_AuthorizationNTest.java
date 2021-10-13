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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenExceptionOfType;

@SpringBootTest(classes={SecureRaceApp.class, AuthorizationTestHelperConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "debug=true")
@ActiveProfiles({"test","authorities", "authorization"})
@Slf4j
@DisplayName("Part B2: Authorization")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class B2_AuthorizationNTest {
    @Autowired
    private RaceDTOFactory raceDTOFactory;
    @Autowired
    private RacerDTOFactory racerDTOFactory;
    private ServerConfig serverConfig;
    private RacesAPI racesClient;
    private RacersAPI racersClient;
//    private RacerRegistrationsAPI racerRegsClient;

    private RacesAPI altRacesClient;
    private RacersAPI altRacersClient;
//    private RacerRegistrationsAPI altRacerRegsClient;
//    private RacerRegistrationsAPI proxyRacerRegsClient;

    private RacesAPIClient racesAdminClient;
    private RacersAPIClient racersAdminClient;
//    private RacerRegistrationsAPI racerRegsAdminClient;
//    private RacerRegistrationsAPI racerRegsMgrClient;

    @BeforeEach
    void init(@LocalServerPort int port,
              @Autowired @Qualifier("authnUser") RestTemplate authnUser,
              @Autowired RestTemplate adminUser,
              @Autowired RestTemplate mgrUser,
              @Autowired RestTemplate altUser,
              @Autowired RestTemplate proxyUser
        ) {
        serverConfig = new ServerConfig().withPort(port).build();
        racesClient = new RacesAPIClient(authnUser, serverConfig, MediaType.APPLICATION_JSON);
        racersClient = new RacersAPIClient(authnUser, serverConfig, MediaType.APPLICATION_JSON);
//        racerRegsClient = new RacerRegistrationsAPIClient(authnUser, serverConfig, MediaType.APPLICATION_JSON);

        racesAdminClient = new RacesAPIClient(adminUser, serverConfig, MediaType.APPLICATION_JSON);
        racersAdminClient = new RacersAPIClient(adminUser, serverConfig, MediaType.APPLICATION_JSON);
//        racerRegsAdminClient = new RacerRegistrationsAPIClient(adminUser, serverConfig, MediaType.APPLICATION_JSON);
//        racerRegsMgrClient = new RacerRegistrationsAPIClient(mgrUser, serverConfig, MediaType.APPLICATION_JSON);

        altRacesClient = new RacesAPIClient(altUser, serverConfig, MediaType.APPLICATION_JSON);
        altRacersClient = new RacersAPIClient(altUser, serverConfig, MediaType.APPLICATION_JSON);
//        altRacerRegsClient = new RacerRegistrationsAPIClient(altUser, serverConfig, MediaType.APPLICATION_JSON);
//        proxyRacerRegsClient = new RacerRegistrationsAPIClient(proxyUser, serverConfig, MediaType.APPLICATION_JSON);


//        racerRegsAdminClient.deleteAllRegistrations();
        racersAdminClient.deleteAllRacers();
        racesAdminClient.deleteAllRaces();
    }

    @Nested
    @Disabled
    class races {
        @Nested
        class authenticated_user_may {
            @Test
            void create_race() {
                //given
                RaceDTO validRace = raceDTOFactory.make();
                //when
                ResponseEntity<RaceDTO> response = racesClient.createRace(validRace);
                //then
                then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            }
            @Test
            void modify_their_races() {
                //given
                RaceDTO existingRace = racesClient.createRace(raceDTOFactory.make()).getBody();
                RaceDTO modifiedRace = existingRace.withName(existingRace.getName() + " modified").withId(null);
                //when
                ResponseEntity<RaceDTO> response = racesClient.updateRace(existingRace.getId(), modifiedRace);
                //then
                then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                RaceDTO updatedRace = response.getBody();
                then(updatedRace.getName()).isEqualTo(modifiedRace.getName()).contains("modified");
            }
            @Test
            void delete_their_races() {
                //given
                RaceDTO existingRace = racesClient.createRace(raceDTOFactory.make()).getBody();
                //when
                ResponseEntity response = racesClient.deleteRace(existingRace.getId());
                //then
                then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
                RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
                        ()->racesClient.getRace(existingRace.getId()));
                then(ex.getRawStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            }
        }
        @Nested
        class authenticated_user_may_not {
            @Test
            void modify_anothers_races() {
                //given
                RaceDTO existingRace = racesClient.createRace(raceDTOFactory.make()).getBody();
                RaceDTO modifiedRace = existingRace.withName(existingRace.getName() + " modified").withId(null);
                //when
                RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
                        () -> altRacesClient.updateRace(existingRace.getId(), modifiedRace));
                //then
                then(ex.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            }
            @Test
            void delete_anothers_races() {
                //given
                RaceDTO existingRace = racesClient.createRace(raceDTOFactory.make()).getBody();
                //when
                RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
                        ()->altRacesClient.deleteRace(existingRace.getId()));
                //then
                then(ex.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            }
            @Test
            void delete_all_races() {
                //given
                RaceDTO existingRace = racesClient.createRace(raceDTOFactory.make()).getBody();
                //when
                RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
                        () -> racesClient.deleteAllRaces());
                //then
                then(ex.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
                then(racesClient.getRace(existingRace.getId()).getStatusCode()).isEqualTo(HttpStatus.OK);
                then(racesClient.getRaces(1,0).getBody().getRaces()).isNotEmpty();
            }
        }

        @Nested
        class unauthenticated_user {
            private RacesAPI anonymousRacesClient;
            @BeforeEach
            void init() {
                anonymousRacesClient = new RacesAPIClient(new RestTemplate(), serverConfig, MediaType.APPLICATION_JSON);
            }

            @Test
            void may_not_create_race() {
                //given
                RaceDTO validRace = raceDTOFactory.make();
                //when
                RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
                        () -> anonymousRacesClient.createRace(validRace));
                //then
                then(ex.getRawStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
            }
        }

        @Nested
        class admin_user {
            @Test
            void can_delete_all_races() {
                //given
                RaceDTO existingRace = racesClient.createRace(raceDTOFactory.make()).getBody();
                //when
                ResponseEntity response = racesAdminClient.deleteAllRaces();
                //then
                then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            }
        }
    }

    @Nested
    class racers {
        @Nested
        @Disabled("TODO")
        class authenticated_user_may {
            @Test
            void may_create_their_racer() {
                //given
                RacerDTO validRacer = racerDTOFactory.make();
                //when
                ResponseEntity response = racersClient.createRacer(validRacer);
                //then
                then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            }
            @Test
            void modify_their_racer() {
                //given
                RacerDTO existingRacer = racersClient.createRacer(racerDTOFactory.make()).getBody();
                RacerDTO modifiedRacer = existingRacer.withFirstName(existingRacer.getFirstName() + " modified");
                //when
                ResponseEntity<RacerDTO> response = racersClient.updateRacer(existingRacer.getId(), modifiedRacer);
                //then
                then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                RacerDTO updatedRacer = response.getBody();
                then(updatedRacer.getFirstName()).isEqualTo(modifiedRacer.getFirstName()).contains("modified");
            }
            @Test
            void delete_their_racer() {
                //given
                RacerDTO existingRacer = racersClient.createRacer(racerDTOFactory.make()).getBody();
                //when
                ResponseEntity response = racersClient.deleteRacer(existingRacer.getId());
                //then
                then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
                thenExceptionOfType(RestClientResponseException.class)
                        .isThrownBy(()->racersClient.getRacer(existingRacer.getId()))
                        .withMessageContaining("not found");
            }
        }

        @Nested
        @Disabled("TODO")
        class authenticated_user_may_not {
            @Test
            void modify_anothers_racer() {
                //given
                RacerDTO existingRacer = racersClient.createRacer(racerDTOFactory.make()).getBody();
                RacerDTO modifiedRacer = existingRacer.withFirstName(existingRacer.getFirstName() + " modified");
                //when
                RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
                        () -> altRacersClient.updateRacer(existingRacer.getId(), modifiedRacer));
                //then
                then(ex.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            }
            @Test
            void delete_anothers_racer() {
                //given
                RacerDTO existingRacer = racersClient.createRacer(racerDTOFactory.make()).getBody();
                //when
                RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
                        () -> altRacersClient.deleteRacer(existingRacer.getId()));
                //then
                then(ex.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            }
            @Test
            void delete_all_racers() {
                //given
                RacerDTO existingRacer = racersClient.createRacer(racerDTOFactory.make()).getBody();
                //when
                RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
                        () -> racersClient.deleteAllRacers());
                //then
                then(ex.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
                then(racersClient.getRacers(1,0).getBody().getRacers()).isNotEmpty();
            }
        }

        @Nested
        @Disabled("TODO")
        class unauthenticated_user {
            private RacersAPI anonymousRacersClient;

            @BeforeEach
            void init() {
                anonymousRacersClient = new RacersAPIClient(new RestTemplate(), serverConfig, MediaType.APPLICATION_JSON);
            }

            @Test
            void may_not_create_racer() {
                //given
                RacerDTO validRacer = racerDTOFactory.make();
                //when
                RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
                        () -> anonymousRacersClient.createRacer(validRacer));
                //then
                then(ex.getRawStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
            }
        }

        @Nested
        @Disabled("TODO")
        class admin_user_can {
            @Test
            void delete_all_racers() {
                //given
                RacerDTO existingRacer = racersClient.createRacer(racerDTOFactory.make()).getBody();
                //when
                ResponseEntity response = racersAdminClient.deleteAllRacers();
                //then
                then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
                thenExceptionOfType(RestClientResponseException.class)
                        .isThrownBy(()->racersClient.getRacer(existingRacer.getId()))
                        .withMessageContaining("not found");
            }
        }
    }

    @Nested
    class race_registrations {
        @Nested
        @Disabled("TODO")
        class authenticated_users_may {
            @Test
            void create_registration_for_existing_race_and_their_racer() {
                //given
                RaceDTO existingRace = racesClient.createRace(raceDTOFactory.make()).getBody();
                RacerDTO existingRacer = racersClient.createRacer(racerDTOFactory.make()).getBody();
                //when
//                ResponseEntity<RacerRegistrationDTO> response =
//                        racerRegsClient.createRegistration(existingRace.getId(), existingRacer.getId());
//                //then
//                then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            }
            @Test
            void delete_their_own_registration() {
                //given
                RaceDTO existingRace = racesClient.createRace(raceDTOFactory.make()).getBody();
                RacerDTO existingRacer = racersClient.createRacer(racerDTOFactory.make()).getBody();
//                RacerRegistrationDTO myRegistration =
//                        racerRegsClient.createRegistration(existingRace.getId(), existingRacer.getId()).getBody();
//                //when
//                ResponseEntity<Void> response =
//                        racerRegsClient.deleteRegistration(myRegistration.getId());
//                //then
//                then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            }
        }

        @Nested
        @Disabled("TODO")
        class authenticated_users_may_not {
            @Test
            void create_registration_for_existing_race_and_other_racer() {
                //given
                RaceDTO existingRace = racesClient.createRace(raceDTOFactory.make()).getBody();
                RacerDTO existingRacer = racersClient.createRacer(racerDTOFactory.make()).getBody();
//                //when
//                RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
//                        () -> altRacerRegsClient.createRegistration(existingRace.getId(), existingRacer.getId()));
//                //then
//                then(ex.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            }
            @Test
            void delete_anothers_registration() {
                //given
                RaceDTO existingRace = racesClient.createRace(raceDTOFactory.make()).getBody();
                RacerDTO existingRacer = racersClient.createRacer(racerDTOFactory.make()).getBody();
//                RacerRegistrationDTO myRegistration =
//                        racerRegsClient.createRegistration(existingRace.getId(), existingRacer.getId()).getBody();
//                //when
//                RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
//                        () -> altRacerRegsClient.deleteRegistration(myRegistration.getId()));
//                //then
//                then(ex.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            }
            @Test
            void cannot_delete_all_registrations() {
                //given
                RaceDTO existingRace = racesClient.createRace(raceDTOFactory.make()).getBody();
                RacerDTO existingRacer = racersClient.createRacer(racerDTOFactory.make()).getBody();
//                RacerRegistrationDTO existingReg = racerRegsClient.createRegistration(
//                        existingRace.getId(), existingRacer.getId()).getBody();
//                //when
//                RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
//                        () -> racerRegsClient.deleteAllRegistrations());
//                //then
//                then(ex.getRawStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
//                then(racerRegsClient.getRegistrations(1,0).getBody().getRegistrations()).isNotEmpty();
            }
        }

        @Nested
        @Disabled("TODO")
        class proxy_may {
            @Test
            void register_another_user_for_race() {
                //given
                RaceDTO existingRace = racesClient.createRace(raceDTOFactory.make()).getBody();
                RacerDTO otherRacer = racersClient.createRacer(racerDTOFactory.make()).getBody();
//                //when
//                ResponseEntity<RacerRegistrationDTO> response =
//                        proxyRacerRegsClient.createRegistration(existingRace.getId(), otherRacer.getId());
//                //then
//                then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            }
        }

        @Nested
        @Disabled("TODO")
        class mgr {
            @Test
            void register_another_user_for_race() {
                //given
                RaceDTO existingRace = racesClient.createRace(raceDTOFactory.make()).getBody();
                RacerDTO otherRacer = racersClient.createRacer(racerDTOFactory.make()).getBody();
//                //when
//                ResponseEntity<RacerRegistrationDTO> response =
//                        racerRegsMgrClient.createRegistration(existingRace.getId(), otherRacer.getId());
//                //then
//                then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            }

            @Test
            void may_delete_registration() {
                //given
                RaceDTO existingRace = racesClient.createRace(raceDTOFactory.make()).getBody();
                RacerDTO existingRacer = racersClient.createRacer(racerDTOFactory.make()).getBody();
//                RacerRegistrationDTO existingReg = racerRegsClient.createRegistration(
//                        existingRace.getId(), existingRacer.getId()).getBody();
//                //when
//                ResponseEntity response = racerRegsMgrClient.deleteRegistration(existingReg.getId());
//                //then
//                then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
//                RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
//                        ()-> racerRegsClient.getRegistration(existingReg.getId()));
//                then(ex.getRawStatusCode()).isEqualTo((HttpStatus.NOT_FOUND.value()));
            }
        }

        @Nested
        @Disabled("TODO")
        class admin_can {
            @Test
            void delete_all_registrations() {
                //given
                RaceDTO existingRace = racesClient.createRace(raceDTOFactory.make()).getBody();
                RacerDTO existingRacer = racersClient.createRacer(racerDTOFactory.make()).getBody();
//                RacerRegistrationDTO existingReg = racerRegsClient.createRegistration(
//                        existingRace.getId(), existingRacer.getId()).getBody();
//                //when
//                ResponseEntity response = racerRegsAdminClient.deleteAllRegistrations();
//                //then
//                then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
//                RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
//                        ()-> racerRegsClient.getRegistration(existingReg.getId()));
//                then(ex.getRawStatusCode()).isEqualTo((HttpStatus.NOT_FOUND.value()));
            }

            @Test
            void inherit_delete_authority_from_mgr() {
                //given
                RaceDTO existingRace = racesClient.createRace(raceDTOFactory.make()).getBody();
                RacerDTO existingRacer = racersClient.createRacer(racerDTOFactory.make()).getBody();
//                RacerRegistrationDTO existingReg = racerRegsClient.createRegistration(
//                        existingRace.getId(), existingRacer.getId()).getBody();
//                //when
//                ResponseEntity response = racerRegsAdminClient.deleteRegistration(existingReg.getId());
//                //then
//                then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
//                RestClientResponseException ex = Assertions.assertThrows(RestClientResponseException.class,
//                        ()-> racerRegsClient.getRegistration(existingReg.getId()));
//                then(ex.getRawStatusCode()).isEqualTo((HttpStatus.NOT_FOUND.value()));
            }
        }
    }

}

