package info.ejava.examples.svc.swagger.contests;

import info.ejava.examples.common.dto.JsonUtil;
import info.ejava.examples.common.web.ServerConfig;
import info.ejava.examples.svc.springdoc.contests.ContestApplication;
import info.ejava.examples.svc.springdoc.contests.api.ContestAPI;
import info.ejava.examples.svc.springdoc.contests.client.ContestsWebClientImpl;
import info.ejava.examples.svc.springdoc.contests.dto.ContestDTO;
import info.ejava.examples.svc.springdoc.contests.dto.ContestDTOFactory;
import info.ejava.examples.svc.springdoc.contests.dto.ContestListDTO;
import info.ejava.examples.svc.springdoc.contests.dto.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes={ContestApplication.class, ClientTestConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "test=true")
@ActiveProfiles("test")
@Slf4j
public class ContestNTest {
    @Autowired
    private ContestDTOFactory contestDTOFactory;
    private URI baseUrl;
    private ContestAPI contestsClient;

    @BeforeEach
    public void setUp(@LocalServerPort int port,
                      @Autowired WebClient webClient) {
        ServerConfig serverConfig = new ServerConfig().withPort(port).build();
        contestsClient = new ContestsWebClientImpl(webClient, serverConfig, MediaType.APPLICATION_JSON_VALUE);
        baseUrl = serverConfig.getBaseUrl();

        log.info("clearing all gestures");
        contestsClient.deleteAllContests().block();
    }


    @Test
    public void add_valid_contest() {
        //given a valid contest
        ContestDTO contestToAdd = contestDTOFactory.make();

        //when adding a valid contest
        ResponseEntity<ContestDTO> response = contestsClient.createContest(contestToAdd).block();

        //then we should get a valid status back with contest with assigned ID
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ContestDTO createdContest = response.getBody();
        then(createdContest.getId()).isGreaterThan(0);
        then(createdContest).isEqualTo(contestToAdd.withId(createdContest.getId()));
    }


    @Test
    public void add_invalid_contest_rejected() {
        //given a invalid contest
        ContestDTO contestToAdd = contestDTOFactory.make().withAwayTeam(null);

        //when adding a invalid contest
        WebClientResponseException ex = Assertions.catchThrowableOfType(
                ()->contestsClient.createContest(contestToAdd).block(),
                WebClientResponseException.UnprocessableEntity.class);

        //then the payload entity will be rejected
        then(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        MessageDTO errMessage = JsonUtil.instance().unmarshal(ex.getResponseBodyAsByteArray(), MessageDTO.class);
        then(errMessage.getText()).contains("away team");
    }

    private Map<Integer, ContestDTO> given_many_contests(int count) {
        return contestDTOFactory.listBuilder().quotes(20, 20)
                .stream()
                .map(contest->contestsClient.createContest(contest).block().getBody())
                .collect(Collectors.toMap(contest->contest.getId(), contest->contest));
    }

    @Test
    public void get_many_contests() {
        //given many contests available
        Map<Integer, ContestDTO> createdContests = given_many_contests(20);

        //when requesting most contests
        ResponseEntity<ContestListDTO> response = contestsClient.getContests(3, 15).block();

        //then we will get a page of contests
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ContestListDTO contests = response.getBody();
        for (ContestDTO c: contests.getContests()) {
            ContestDTO expectedContest = createdContests.get(c.getId());
            assertThat(expectedContest).isNotNull();
            assertThat(c).isEqualTo(expectedContest);
        }

        //and the metadata for the collection is correct
        then(contests.getOffset()).isEqualTo(3);
        then(contests.getLimit()).isEqualTo(15);
        then(contests.getTotal()).isEqualTo(createdContests.size());
    }

    @Test
    public void get_existing_contest() {
        //given an existing contest
        ContestDTO existingContest = contestsClient.createContest(contestDTOFactory.make()).block().getBody();

        //when requesting that contest
        ResponseEntity<ContestDTO> response = contestsClient.getContest(existingContest.getId()).block();

        //then we get back that contest
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isEqualTo(existingContest);
    }

    @Test
    public void get_contest_not_found() {
        //given an existing contest
        int contestId = 113;

        //when requesting that contest
        WebClientResponseException ex = catchThrowableOfType(
                () -> contestsClient.getContest(contestId).block(),
                WebClientResponseException.NotFound.class);

        //then not found will tell is the ID we searched for
        MessageDTO errMessage = JsonUtil.instance().unmarshal(ex.getResponseBodyAsByteArray(), MessageDTO.class);
        then(errMessage.getText()).contains("contest["+ contestId +"]");
    }


    @Test
    public void check_existing_contest() {
        //given an existing contest
        ContestDTO existingContest = contestsClient.createContest(contestDTOFactory.make()).block().getBody();

        //when requesting that contest
        ResponseEntity<Void> response = contestsClient.containsContest(existingContest.getId()).block();

        //then we get back that contest
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isNull();
    }

    @Test
    public void check_contest_not_found() {
        //given an existing contest
        int contestId = 113;

        //when checking that contest
        WebClientResponseException ex = catchThrowableOfType(
                () -> contestsClient.containsContest(contestId).block(),
                WebClientResponseException.NotFound.class);

        //then there is no error message
        then(ex.getResponseBodyAsString()).isEmpty();
    }

    @Test
    public void delete_all_contests() {
        //given
        Map<Integer, ContestDTO> contestsMap = given_many_contests(5);
        ContestDTO sampleContest = contestsMap.values().iterator().next();
        assertThat(contestsClient.containsContest(sampleContest.getId()).block().getStatusCode()).isEqualTo(HttpStatus.OK);

        //when deleting all contests
        ResponseEntity<Void> response = contestsClient.deleteAllContests().block();

        //then valid response is returned and sample contest is gone
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        WebClientResponseException ex = catchThrowableOfType(
                ()->contestsClient.containsContest(sampleContest.getId()).block(),
                WebClientResponseException.NotFound.class);
    }

    @Test
    public void update_existing_contest() {
        //given an unscheduled contest
        ContestDTO existingContest = contestsClient.createContest(
                contestDTOFactory.make()
                    .withCompleted(false)
                    .withHomeScore(null)
                    .withAwayScore(null))
                .block().getBody();

        //when updating an existing contest
        ContestDTO updateSent = existingContest.withCompleted(true).withHomeScore(5).withHomeScore(2);
        ResponseEntity<Void> response = contestsClient.updateContest(existingContest.getId(), updateSent).block();

        //then we get a success response
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        //contest was updated
        ContestDTO currentContestValue = contestsClient.getContest(existingContest.getId()).block().getBody();
        then(currentContestValue).isEqualTo(updateSent);
    }

    @Test
    public void update_non_existing_contest_fails() {
        //given a non-existant contestId
        int contestId = 113;
        ContestDTO updateSent = contestDTOFactory.make();

        //when attempting to update a non-existant contest
        WebClientResponseException ex = catchThrowableOfType(
                ()->contestsClient.updateContest(contestId, updateSent).block(),
                WebClientResponseException.NotFound.class);

        //then the error message will state the ID we asked for
        MessageDTO errMessage = JsonUtil.instance().unmarshal(ex.getResponseBodyAsByteArray(), MessageDTO.class);
        then(errMessage.getText()).contains("contest["+ contestId +"]");
    }
}
