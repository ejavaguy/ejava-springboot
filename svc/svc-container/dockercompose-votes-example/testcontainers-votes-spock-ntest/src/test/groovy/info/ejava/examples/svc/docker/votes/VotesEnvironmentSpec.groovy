package info.ejava.examples.svc.docker.votes

import com.mongodb.client.MongoClient
import groovy.util.logging.Slf4j
import info.ejava.examples.svc.docker.votes.dto.ElectionResultsDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jms.core.JmsTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import org.testcontainers.containers.DockerComposeContainer
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

import static org.assertj.core.api.Assertions.assertThat

@SpringBootTest(classes = [ClientTestConfiguration.class, VoterListener.class],
    webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(initializers = Initializer.class) //way2
@Stepwise
@Slf4j
@DirtiesContext
abstract class VotesEnvironmentSpec extends Specification {
    private static DockerComposeContainer staticEnv
    //dynamic initialization -- way 3
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        ClientTestConfiguration.initProperties(registry, staticEnv)
    }

    //dynamic initialization -- way 2
    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        void initialize(ConfigurableApplicationContext ctx) {
//            ClientTestConfiguration.initProperties(ctx, staticEnv)
        }
    }

    @Shared
    protected DockerComposeContainer env = ClientTestConfiguration.testEnvironment()
    @Autowired
    protected RestTemplate restTemplate
    @Autowired
    protected URI votesUrl
    @Autowired
    protected URI electionsUrl
    @Autowired
    protected MongoClient mongoClient
    @Autowired
    protected JmsTemplate jmsTemplate
    @Autowired
    protected JdbcTemplate jdbcTemplate
    @Autowired
    protected VoterListener listener

    def setupSpec() {
        staticEnv = env
        configureEnv(env)
        env.start()
        //dynamic initialization -- way 1
        //ClientTestConfiguration.initProperties(env)
    }
    void configureEnv(DockerComposeContainer env) {}
    def cleanupSpec() {
        env.stop()
    }

    def setup() {
        log.info("votesUrl={}", votesUrl)
        log.info("electionsUrl={}", electionsUrl)
    }

    public ElectionResultsDTO wait_for_results(Instant resultTime) {
        ElectionResultsDTO newResults=get_election_counts()
        int calls=1
        for (int i=calls; newResults.getDate().isBefore(resultTime) && i<10000; i++) {
            log.info("checking election ... {}, {}, {}", calls++, resultTime, newResults.getDate())
            newResults = get_election_counts()
            log.info("results={}", newResults)
        }
        log.info("done checking election after {} calls, {} {}", calls, resultTime, newResults.getDate())
        log.trace("results={}", newResults)
        return newResults
    }

    public ElectionResultsDTO get_election_counts() {
        URI url = UriComponentsBuilder.fromUri(electionsUrl).path("/counts").build().toUri()
        RequestEntity<Void> request = RequestEntity.get(url).build()

        ResponseEntity<ElectionResultsDTO> response = restTemplate.exchange(request, ElectionResultsDTO.class)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK)

        ElectionResultsDTO results = response.getBody()
        if (results.getDate()==null) {
            results.setDate(LocalDate.of(1970, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant())
        }
        return results
    }

}
