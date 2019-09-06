package info.ejava.examples.svc.docker.votes.services;

import info.ejava.examples.svc.docker.votes.bo.VoteBO;
import info.ejava.examples.svc.docker.votes.dto.ElectionResultsDTO;
import info.ejava.examples.svc.docker.votes.dto.VoteCountDTO;
import info.ejava.examples.svc.docker.votes.dto.VoteDTO;
import info.ejava.examples.svc.docker.votes.repos.ElectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElectionsServiceImpl implements ElectionsService {
    private final ElectionRepository votesRepository;

    @Override
    @Transactional(value = Transactional.TxType.REQUIRED)
    public void addVote(VoteDTO voteDTO) {
        VoteBO vote = map(voteDTO);
        votesRepository.save(vote);
        log.info("added new vote {}", vote);
    }

    @Override
    public ElectionResultsDTO getVoteCounts() {
        List<Object[]> counts = votesRepository.countVotes();

        ElectionResultsDTO electionResults = new ElectionResultsDTO();
        List<VoteCountDTO> choices = new ArrayList<>(counts.size());
        Instant lastResult = LocalDateTime.of(1970, 1, 1, 0, 0).toInstant(ZoneOffset.UTC);
        for(Object[] result: counts) {
            VoteCountDTO choice = new VoteCountDTO(
                    (String)result[0],
                    ((Number)result[1]).intValue());
            Instant resultTime = ((Timestamp)result[2]).toInstant();
            if (lastResult.isBefore(resultTime)) {
                log.info("resultTime={}, lastResult={}", resultTime, lastResult);
                lastResult = resultTime;
            }
            choices.add(choice);
        }
        electionResults.setDate(lastResult);
        electionResults.setResults(choices);
        log.info("lastResult={}", lastResult);

        return electionResults;
    }

    protected VoteBO map(VoteDTO dto) {
        if (dto==null) { return null; }

        return VoteBO.builder()
                .id(dto.getId())
                .date(Date.from(dto.getDate()))
                .source(dto.getSource())
                .choice(dto.getChoice())
                .build();
    }
}
