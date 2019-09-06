package info.ejava.examples.svc.docker.votes;

import info.ejava.examples.svc.docker.votes.dto.ElectionResultsDTO;
import info.ejava.examples.svc.docker.votes.dto.VoteDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest( classes = {ClientTestConfiguration.class, VotesExampleApp.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "test")
@ActiveProfiles("test")
@Slf4j
@DisplayName("votes integration unit test")
public class VotesTemplateNTest {
    @Autowired
    private RestTemplate restTemplate;
    private final URI baseVotesUrl;
    private final URI baseElectionsUrl;

    public VotesTemplateNTest(@LocalServerPort int port) throws URISyntaxException {
        baseVotesUrl = new URI(String.format("http://localhost:%d/api/votes", port));
        baseElectionsUrl = new URI(String.format("http://localhost:%d/api/elections", port));
        log.info("baseUrl={}", baseVotesUrl);
    }

    VoteDTO create_vote(String voter, String choice) {
        return VoteDTO.builder()
                .source(voter)
                .choice(choice)
                .build();
    }

    @Test
    public void cast_vote() {
        //given - a vote to cast
        Instant before = Instant.now();
        URI url = baseVotesUrl;
        VoteDTO voteCast = create_vote("voter1","quisp");
        RequestEntity<VoteDTO> request = RequestEntity.post(url).body(voteCast);

        //when - vote is casted
        ResponseEntity<VoteDTO> response = restTemplate.exchange(request, VoteDTO.class);

        //then - vote is created
        then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        VoteDTO recordedVote = response.getBody();
        then(recordedVote.getId()).isNotEmpty();
        then(recordedVote.getDate()).isAfterOrEqualTo(before);
        then(recordedVote.getSource()).isEqualTo(voteCast.getSource());
        then(recordedVote.getChoice()).isEqualTo(voteCast.getChoice());
    }

    int get_current_vote_total() {
        URI url = UriComponentsBuilder.fromUri(baseVotesUrl).path("/total").build().toUri();
        RequestEntity<Void> request = RequestEntity.get(url).build();

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return Integer.parseInt(response.getBody());
    }

    @Test
    public void vote_recorded() {
        //given
        int existing_votes = get_current_vote_total();
        VoteDTO voteCast = create_vote("voter1","quisp");
        RequestEntity<VoteDTO> request = RequestEntity.post(baseVotesUrl).body(voteCast);
        ResponseEntity<VoteDTO> response = restTemplate.exchange(request, VoteDTO.class);

        //when
        int total_votes = get_current_vote_total();

        //then
        then(total_votes).isEqualTo(existing_votes+1);
    }

    public ElectionResultsDTO get_election_counts() {
        URI url = UriComponentsBuilder.fromUri(baseElectionsUrl).path("/counts").build().toUri();
        RequestEntity<Void> request = RequestEntity.get(url).build();

        ResponseEntity<ElectionResultsDTO> response = restTemplate.exchange(request, ElectionResultsDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ElectionResultsDTO results = response.getBody();
        if (results.getDate()==null) {
            results.setDate(LocalDate.of(1970, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant());
        }
        return results;
    }

    @Test
    public void vote_counted_in_election() {
        //given
        ElectionResultsDTO previousResults = get_election_counts();
        VoteDTO voteCast = create_vote("voter1", UUID.randomUUID().toString());
        RequestEntity<VoteDTO> request = RequestEntity.post(baseVotesUrl).body(voteCast);
        ResponseEntity<VoteDTO> response = restTemplate.exchange(request, VoteDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //when
        ElectionResultsDTO newResults=get_election_counts();
        int calls=1;
        while (!newResults.getDate().isAfter(previousResults.getDate()) && calls<10000) {
            log.info("checking election ... {}, {}, {}", calls, previousResults.getDate(), newResults.getDate());
            newResults = get_election_counts();
            calls += 1;
            try { Thread.sleep(1000); } catch (Exception ex){}
        }
        log.info("done checking election after {} calls, {} {}", calls, previousResults.getDate(), newResults.getDate());

        //then
        then(newResults.getResults().size()).isEqualTo(previousResults.getResults().size()+1);
        then(newResults.getChoice(voteCast.getChoice())).isNotNull();
        then(newResults.getChoice(voteCast.getChoice()).getVotes()).isEqualTo(1);
    }
}
