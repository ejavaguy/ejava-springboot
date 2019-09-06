package info.ejava.examples.svc.docker.votes.services;

import info.ejava.examples.svc.docker.votes.dto.VoteDTO;
import org.springframework.data.domain.Page;

public interface VoterService {
    VoteDTO castVote(VoteDTO newVote);
    long getTotalVotes();
    Page<VoteDTO> getVotes(int offset, int limit);
}
