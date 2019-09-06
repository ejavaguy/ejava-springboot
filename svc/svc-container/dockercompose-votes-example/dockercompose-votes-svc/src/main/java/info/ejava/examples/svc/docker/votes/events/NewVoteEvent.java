package info.ejava.examples.svc.docker.votes.events;

import info.ejava.examples.svc.docker.votes.dto.VoteDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NewVoteEvent {
    private final VoteDTO vote;
}
