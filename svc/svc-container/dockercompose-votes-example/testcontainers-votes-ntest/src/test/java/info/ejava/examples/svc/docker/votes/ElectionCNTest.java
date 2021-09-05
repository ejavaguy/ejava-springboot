package info.ejava.examples.svc.docker.votes;

import com.mongodb.client.MongoClient;
import info.ejava.examples.svc.docker.votes.dto.ElectionResultsDTO;
import info.ejava.examples.svc.docker.votes.dto.VoteDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes={ClientTestConfiguration.class, VoterListener.class},
    webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
public class ElectionCNTest {
    @Container
    private static DockerComposeContainer env = ClientTestConfiguration.testEnvironment();
    @DynamicPropertySource
    private static void properties(DynamicPropertyRegistry registry) {
        ClientTestConfiguration.initProperties(registry, env);
    }
    @AfterAll
    public static void tearDownClass() {
        if (env!=null) { env.close(); }
    }

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private URI votesUrl;
    @Autowired
    private URI electionsUrl;

    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private VoterListener listener;

    @BeforeEach
    public void init() throws IOException, InterruptedException {
        log.info("votesUrl={}", votesUrl);
        log.info("electionsUrl={}", electionsUrl);

        /**
         * wait for various events relative to our containers
         */
        env.waitingFor("api", Wait.forHttp(votesUrl.toString()));
        env.waitingFor("api", Wait.forHttp(electionsUrl.toString()));

        /**
         * connect directly to explosed port# of images to obtain sample status
         */
        log.info("mongo client vote count={}",
                mongoClient.getDatabase("votes_db").getCollection("votes").countDocuments());
        log.info("activemq msg={}", listener.getMsgCount().get());
        log.info("postgres client vote count={}",
                jdbcTemplate.queryForObject("select count (*) from vote", Long.class));


        /**
         * run sample commands directly against containers
          */
        ContainerState mongo = (ContainerState) env.getContainerByServiceName("mongo_1").orElseThrow();
        ExecResult result = mongo.execInContainer("mongo",
                "-u", "admin", "-p", "secret", "--authenticationDatabase", "admin",
                "--eval", "db.getSiblingDB('votes_db').votes.find()");
        log.info("voter votes = {}", result.getStdout());

        ContainerState postgres = (ContainerState) env.getContainerByServiceName("postgres_1").orElseThrow();
        result = postgres.execInContainer("psql",
                "-U", "postgres",
                "-c", "select * from vote");
        log.info("election votes = {}", result.getStdout());
    }

    @Test
    @Order(1)
    public void vote_counted_in_election() {
        //given - multiple votes
        ElectionResultsDTO previousResults = get_election_counts();
        VoteDTO[] votesCasted = IntStream.range(0, 2).mapToObj(x->VoteDTO.builder()
                .source(UUID.randomUUID().toString())
                .choice((x%2==0 ? "quisp-" : "quake-") + UUID.randomUUID().toString()).build())
                .toArray(size->new VoteDTO[size]);
        int timesVoted=3;

        //when - many votes are cast
        Instant lastVote = Instant.now();
        for (int i=0; i<timesVoted; i++) {
            for (int v = 0; v < votesCasted.length; v++) {
                RequestEntity<VoteDTO> voteRequest = RequestEntity.post(votesUrl).body(votesCasted[v]);
                ResponseEntity<VoteDTO> voteResponse = restTemplate.exchange(voteRequest, VoteDTO.class);
                assertThat(voteResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                lastVote = Date.from(voteResponse.getBody().getDate()).toInstant(); //put thru Date wash
            }
        }

        ElectionResultsDTO newResults=wait_for_results(lastVote);

        //then
        then(newResults.getDate()).isAfterOrEqualTo(lastVote);
        then(newResults.getResults().size())
                .isGreaterThanOrEqualTo(previousResults.getResults().size()+votesCasted.length);
        for (int i=0; i<votesCasted.length; i++) {
            then(newResults.getChoice(votesCasted[0].getChoice()).getVotes()).isEqualTo(timesVoted);
        }
    }


    @Test
    @Order(3)
    public void test3() {
        vote_counted_in_election();
    }
    @Test
    @Order(2)
    public void test2() {
        vote_counted_in_election();
    }


    public ElectionResultsDTO wait_for_results(Instant resultTime) {
        ElectionResultsDTO newResults=get_election_counts();
        int calls=1;
        for (int i=calls; newResults.getDate().isBefore(resultTime) && i<10000; i++) {
            log.info("checking election ... {}, {}, {}", calls++, resultTime, newResults.getDate());
            newResults = get_election_counts();
            log.info("results={}", newResults);
        }
        log.info("done checking election after {} calls, {} {}", calls, resultTime, newResults.getDate());
        log.trace("results={}", newResults);
        return newResults;
    }

    public ElectionResultsDTO get_election_counts() {
        URI url = UriComponentsBuilder.fromUri(electionsUrl).path("/counts").build().toUri();
        RequestEntity<Void> request = RequestEntity.get(url).build();

        ResponseEntity<ElectionResultsDTO> response = restTemplate.exchange(request, ElectionResultsDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ElectionResultsDTO results = response.getBody();
        if (results.getDate()==null) {
            results.setDate(LocalDate.of(1970, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());
        }
        return results;
    }

}
