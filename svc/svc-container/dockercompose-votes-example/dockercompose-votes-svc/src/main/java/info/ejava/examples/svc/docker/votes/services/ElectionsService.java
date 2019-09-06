package info.ejava.examples.svc.docker.votes.services;

import info.ejava.examples.svc.docker.votes.dto.ElectionResultsDTO;
import info.ejava.examples.svc.docker.votes.dto.VoteDTO;

public interface ElectionsService {
    void addVote(VoteDTO voteDTO);
    ElectionResultsDTO getVoteCounts();
}
