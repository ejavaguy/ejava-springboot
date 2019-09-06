package info.ejava.examples.svc.docker.votes

import groovy.util.logging.Slf4j
import info.ejava.examples.svc.docker.votes.dto.ElectionResultsDTO
import info.ejava.examples.svc.docker.votes.dto.VoteDTO
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.testcontainers.containers.ContainerState
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.output.OutputFrame
import org.testcontainers.containers.wait.strategy.Wait

import java.time.Instant
import java.util.stream.IntStream

import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.BDDAssertions.then

@Slf4j
class Election3CNSpec extends VotesEnvironmentSpec {
    def setup() {
        /**
         * wait for various events relative to our containers
         */
        env.waitingFor("api", Wait.forHttp(votesUrl.toString()))
        env.waitingFor("api", Wait.forHttp(electionsUrl.toString()))

        /**
         * connect directly to explosed port# of images to obtain sample status
         */
        log.info("postgres client vote count={}",
                jdbcTemplate.queryForObject("select count (*) from vote", Long.class))
        log.info("activemq msg={}", listener.getMsgCount().get())
        log.info("mongo client vote count={}",
                mongoClient.getDatabase("votes_db").getCollection("votes").countDocuments())


        /**
         * run sample commands directly against containers
         */
        ContainerState postgres = (ContainerState) env.getContainerByServiceName("postgres_1").orElseThrow()
        org.testcontainers.containers.Container.ExecResult result = postgres.execInContainer("psql",
                "-U", "postgres",
                "-c", "select * from vote");
        log.info("election votes = {}", result.getStdout())

        ContainerState mongo = (ContainerState) env.getContainerByServiceName("mongo_1").orElseThrow()
        result = mongo.execInContainer("mongo",
                "-u", "admin", "-p", "secret", "--authenticationDatabase", "admin",
                "--eval", "db.getSiblingDB('votes_db').votes.find()");
        log.info("voter votes = {}", result.getStdout())
    }

    def "vote counted in election"() {
        given: //multiple votes
        ElectionResultsDTO previousResults = get_election_counts()
        VoteDTO[] votesCasted = IntStream.range(0, 2).mapToObj(x->VoteDTO.builder()
                .source(UUID.randomUUID().toString())
                .choice((x%2==0 ? "quisp-" : "quake-") + UUID.randomUUID().toString()).build())
                .toArray(size->new VoteDTO[size])
        int timesVoted=3

        when: //many votes are cast
        Instant lastVote = Instant.now()
        for (int i=0; i<timesVoted; i++) {
            for (int v = 0; v < votesCasted.length; v++) {
                RequestEntity<VoteDTO> voteRequest = RequestEntity.post(votesUrl).body(votesCasted[v])
                ResponseEntity<VoteDTO> voteResponse = restTemplate.exchange(voteRequest, VoteDTO.class)
                assertThat(voteResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED)
                lastVote = Date.from(voteResponse.getBody().getDate()).toInstant() //put thru Date wash
            }
        }

        ElectionResultsDTO newResults=wait_for_results(lastVote);

        then:
        then(newResults.getDate()).isAfterOrEqualTo(lastVote)
        then(newResults.getResults().size())
                .isGreaterThanOrEqualTo(previousResults.getResults().size()+votesCasted.length)
        for (int i=0; i<votesCasted.length; i++) {
            then(newResults.getChoice(votesCasted[0].getChoice()).getVotes()).isEqualTo(timesVoted)
        }

        where:
            iteration << (1..3)
    }


    def "test 2"() {
        expect:
        1==1
    }

    def "test 3"() {
        expect:
        1==1
    }
}
