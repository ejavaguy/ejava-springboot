package info.ejava.assignments.api.race.races;

import info.ejava.assignments.api.race.config.RaceTestConfiguration;
import info.ejava.assignments.api.race.client.factories.RaceDTOFactory;
import info.ejava.assignments.api.race.client.races.RaceDTO;
import info.ejava.assignments.api.race.client.races.RaceListDTO;
import info.ejava.assignments.api.race.client.races.RacesAPI;
import info.ejava.assignments.api.race.client.races.RacesAPIClient;
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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes = {RaceTestConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "test=true")
@ActiveProfiles("test")
@Tag("springboot")
@Slf4j
public class RacesApiNTest {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RaceDTOFactory raceDTOFactory;

    private RacesAPI racesApiClient;

    @BeforeEach
    void setUp(@LocalServerPort int port) {
        ServerConfig serverConfig = new ServerConfig().withPort(port).build();
        racesApiClient = new RacesAPIClient(restTemplate, serverConfig, MediaType.APPLICATION_JSON);
    }

    @Test
    void can_create_race() {
        //given
        RaceDTO raceRequestDTO=raceDTOFactory.make(RaceDTOFactory.withId);
        //when
        ResponseEntity<RaceDTO> response = racesApiClient.createRace(raceRequestDTO);
        RaceDTO raceResult = response.getBody();
        //then
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        then(raceResult.getId()).isNotBlank();

        URI uri = UriComponentsBuilder.fromUriString(RacesAPI.RACE_PATH).build(raceResult.getId());
        then(response.getHeaders().getLocation().toString()).endsWith(uri.toString());
        then(raceResult).isEqualTo(raceRequestDTO.withId(raceResult.getId()));
    }

    private List<RaceDTO> populate(int count) {
        //fake populate
        //return Arrays.asList(RaceDTO.builder().id("1").build());
        return IntStream.range(0, count)
                .mapToObj(i -> raceDTOFactory.make())
                .map(r -> racesApiClient.createRace(raceDTOFactory.make()).getBody())
                .collect(Collectors.toList());
    }

    static List<RaceDTO> queryRaces = null;
    @Nested
    class Queries {
        @BeforeEach
        void init() {
            if (queryRaces==null) {
                racesApiClient.deleteAllRaces();
                queryRaces=populate(20);
            }
        }

        @Test
        void can_get_races() {
            //given
            List<RaceDTO> expectedRaces = queryRaces;
            //when
            ResponseEntity<RaceListDTO> response = racesApiClient.getRaces(null, null);
            RaceListDTO races = response.getBody();
            //then
            then(response.getStatusCode().is2xxSuccessful()).isTrue();
            then(races.getRaces()).isNotNull();
            then(new HashSet<>(races.getRaces())).isEqualTo(new HashSet<>(expectedRaces));
        }

        @Test
        void can_get_races_paged() {
            //given
            List<RaceDTO> expectedRaces = queryRaces;
            int pageSize=3;
            int pageNum=0;
            List<RaceDTO> returnedRaces = new ArrayList<>();
            do {
                //when
                ResponseEntity<RaceListDTO> response = racesApiClient.getRaces(pageSize, pageNum);
                RaceListDTO races = response.getBody();
                //then
                then(response.getStatusCode().is2xxSuccessful()).isTrue();
                then(races.getRaces()).isNotNull();
                then(races.getRaces().size()).isLessThanOrEqualTo(pageSize);
                returnedRaces.addAll(races.getRaces());
                if (races.getRaces().isEmpty()) {
                    break;
                }
                pageNum += 1;
            } while (true);

            then(new HashSet<>(returnedRaces)).isEqualTo(new HashSet<>(expectedRaces));
        }

        @Test
        void can_get_a_race() {
            //given
            RaceDTO expectedRace = queryRaces.get(0);
            //when
            ResponseEntity<RaceDTO> response = racesApiClient.getRace(expectedRace.getId());
            RaceDTO race = response.getBody();
            //then
            then(response.getStatusCode().is2xxSuccessful()).isTrue();
            then(race).isEqualTo(expectedRace);
        }
    }

    @Test
    void get_non_existant_race() {
        //given
        String nonExistantId = "123000456";
        //when
        RestClientResponseException ex = catchThrowableOfType(() -> racesApiClient.getRace(nonExistantId),
                RestClientResponseException.class);
        //then
        then(ex.getRawStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        MessageDTO errorMsg = JsonUtil.instance().unmarshal(ex.getResponseBodyAsByteArray(), MessageDTO.class);
        then(errorMsg.getDate()).isNotNull();
        then(errorMsg.getDescription()).isEqualTo(String.format("Race[%s] not found", nonExistantId));
    }

    @Test
    void can_update_a_race() {
        //given
        RaceDTO existingRace = populate(1).get(0);
        RaceDTO requestedUpdate = existingRace.withName(existingRace.getName() + "Modified");
        //when
        ResponseEntity<RaceDTO> response = racesApiClient.updateRace(existingRace.getId(), requestedUpdate);
        RaceDTO updatedRace = response.getBody();
        //then
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(updatedRace.getName()).isEqualTo(requestedUpdate.getName());
        then(updatedRace).isEqualTo(requestedUpdate);
    }

    @Test
    void can_delete_a_race() {
        //given
        RaceDTO existingRace = populate(1).get(0);
        //when
        ResponseEntity<Void> response = racesApiClient.deleteRace(existingRace.getId());
        //then
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        RestClientResponseException ex = catchThrowableOfType(
                () -> racesApiClient.getRace(existingRace.getId()),
                RestClientResponseException.class);
        then(ex.getRawStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void can_delete_all_races() {
        //given
        RaceDTO existingRace = populate(10).get(0);
        //when
        ResponseEntity<Void> response = racesApiClient.deleteAllRaces();
        //then
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        RestClientResponseException ex = catchThrowableOfType(
                () -> racesApiClient.getRace(existingRace.getId()),
                RestClientResponseException.class);
        then(ex.getRawStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}


