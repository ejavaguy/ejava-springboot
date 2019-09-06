package info.ejava.examples.svc.docker.votes;

import info.ejava.examples.svc.docker.votes.dto.ElectionResultsDTO;
import info.ejava.examples.svc.docker.votes.dto.VoteDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest(classes={ClientTestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
public class ElectionIT {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private URI votesUrl;
    @Autowired
    private URI electionsUrl;
    private static Boolean serviceAvailable;

    @PostConstruct
    public void init() {
        log.info("votesUrl={}", votesUrl);
        log.info("electionsUrl={}", electionsUrl);
    }

    @BeforeEach
    public void serverRunning() {
        List<URI> urls = new ArrayList<>(Arrays.asList(
                UriComponentsBuilder.fromUri(votesUrl).path("/total").build().toUri(),
                UriComponentsBuilder.fromUri(electionsUrl).path("/counts").build().toUri()
        ));

        if (serviceAvailable!=null) { assumeTrue(serviceAvailable);}
        else {
            assumeTrue(() -> {
                for (int i=0; i<10; i++) {
                    try {
                        for (Iterator<URI> itr = urls.iterator(); itr.hasNext();) {
                            URI url = itr.next();
                            restTemplate.getForObject(url, String.class);
                            itr.remove();
                        }
                        return serviceAvailable = true;
                    } catch (Exception ex) {
                        log.info("waiting for services...{}\n{}", urls, ex.toString());
                        try { Thread.sleep(2000); } catch (Exception e){}
                    }
                }
                return serviceAvailable=false;
            }, String.format("*******\n %s not accessible, server may not available\n *******", urls));
        }
    }

    @Test
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
