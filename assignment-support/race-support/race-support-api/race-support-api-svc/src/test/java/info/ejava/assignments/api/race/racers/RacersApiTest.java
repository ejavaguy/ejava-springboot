package info.ejava.assignments.api.race.racers;

import info.ejava.assignments.api.race.client.factories.RacerDTOFactory;
import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.racers.RacerListDTO;
import info.ejava.assignments.api.race.client.racers.RacersAPI;
import info.ejava.assignments.api.race.client.racers.RacersAPIClient;
import info.ejava.assignments.api.race.config.RaceTestConfiguration;
import info.ejava.examples.common.dto.JsonUtil;
import info.ejava.examples.common.dto.MessageDTO;
import info.ejava.examples.common.web.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes = RaceTestConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "test=true")
@ActiveProfiles("test")
@Tag("springboot")
@Slf4j
public class RacersApiTest {
    @Autowired
    RestTemplate restTemplate;
    private RacersAPI racersApiClient;
    @Autowired
    private RacerDTOFactory racerDTOFactory;

    @BeforeEach
    void setUp(@LocalServerPort int port) {
        ServerConfig serverConfig = new ServerConfig().withPort(port).build();
        racersApiClient = new RacersAPIClient(restTemplate, serverConfig, MediaType.APPLICATION_JSON);
    }

    @Test
    void can_create_racer() {
        //given
        RacerDTO racer = racerDTOFactory.make();
        //when
        ResponseEntity<RacerDTO> response = racersApiClient.createRacer(racer);
        RacerDTO createdRacer = response.getBody();
        //then
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String expectedUri = UriComponentsBuilder.fromPath(RacersAPI.RACER_PATH).build(createdRacer.getId()).toString();
        then(response.getHeaders().getLocation()).isNotNull();
        then(response.getHeaders().getLocation().toString()).endsWith(expectedUri);
        then(createdRacer).isEqualTo(racer.withId(createdRacer.getId()));
    }

    private List<RacerDTO> populate(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> racerDTOFactory.make())
                .map(r -> racersApiClient.createRacer(racerDTOFactory.make()).getBody())
                .collect(Collectors.toList());
    }

    static List<RacerDTO> queryRacers = null;

    @Nested
    class Queries {
        @BeforeEach
        void init() {
            if (queryRacers == null) {
                racersApiClient.deleteAllRacers();
                queryRacers = populate(20);
            }
        }

        @Test
        void can_get_racers() {
            //given
            List<RacerDTO> expectedRacers = queryRacers;
            //when
            ResponseEntity<RacerListDTO> response = racersApiClient.getRacers(null, null);
            RacerListDTO racers = response.getBody();
            //then
            then(response.getStatusCode().is2xxSuccessful()).isTrue();
            then(racers.getRacers()).isNotNull();
            then(new HashSet<>(racers.getRacers())).isEqualTo(new HashSet<>(expectedRacers));
        }

        @Test
        void can_get_racers_paged() {
            //given
            List<RacerDTO> expectedRacers = queryRacers;
            int pageSize = 3;
            int pageNum = 0;
            List<RacerDTO> returnedRacers = new ArrayList<>();
            do {
                //when
                ResponseEntity<RacerListDTO> response = racersApiClient.getRacers(pageSize, pageNum);
                RacerListDTO racers = response.getBody();
                //then
                then(response.getStatusCode().is2xxSuccessful()).isTrue();
                then(racers.getRacers()).isNotNull();
                then(racers.getRacers().size()).isLessThanOrEqualTo(pageSize);
                returnedRacers.addAll(racers.getRacers());
                if (racers.getRacers().isEmpty()) {
                    break;
                }
                pageNum += 1;
            } while (true);

            then(new HashSet<>(returnedRacers)).isEqualTo(new HashSet<>(expectedRacers));
        }

        @Test
        void can_get_a_racer() {
            //given
            RacerDTO expectedRacer = queryRacers.get(0);
            //when
            ResponseEntity<RacerDTO> response = racersApiClient.getRacer(expectedRacer.getId());
            RacerDTO race = response.getBody();
            //then
            then(response.getStatusCode().is2xxSuccessful()).isTrue();
            then(race).isEqualTo(expectedRacer);
        }
    }

    @Test
    void get_non_existant_race() {
        //given
        String nonExistantId = "123000456";
        //when
        RestClientResponseException ex = catchThrowableOfType(() -> racersApiClient.getRacer(nonExistantId),
                RestClientResponseException.class);
        //then
        then(ex.getRawStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        MessageDTO errorMsg = JsonUtil.instance().unmarshal(ex.getResponseBodyAsByteArray(), MessageDTO.class);
        then(errorMsg.getDate()).isNotNull();
        then(errorMsg.getDescription()).isEqualTo(String.format("Racer[%s] not found", nonExistantId));
    }

    @Test
    void can_update_a_race() {
        //given
        RacerDTO existingRacer = populate(1).get(0);
        RacerDTO requestedUpdate = existingRacer.withFirstName(existingRacer.getFirstName() + "Modified");
        //when
        ResponseEntity<RacerDTO> response = racersApiClient.updateRacer(existingRacer.getId(), requestedUpdate);
        RacerDTO updatedRacer = response.getBody();
        //then
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(updatedRacer.getFirstName()).isEqualTo(requestedUpdate.getFirstName());
        then(updatedRacer).isEqualTo(requestedUpdate);
    }

    @Test
    void can_delete_a_race() {
        //given
        RacerDTO existingRacer = populate(1).get(0);
        //when
        ResponseEntity<Void> response = racersApiClient.deleteRacer(existingRacer.getId());
        //then
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        RestClientResponseException ex = catchThrowableOfType(
                () -> racersApiClient.getRacer(existingRacer.getId()),
                RestClientResponseException.class);
        then(ex.getRawStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void can_delete_all_races() {
        //given
        RacerDTO existingRacer = populate(10).get(0);
        //when
        ResponseEntity<Void> response = racersApiClient.deleteAllRacers();
        //then
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        RestClientResponseException ex = catchThrowableOfType(
                () -> racersApiClient.getRacer(existingRacer.getId()),
                RestClientResponseException.class);
        then(ex.getRawStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
