package info.ejava.examples.svc.docker.votes.events;

import info.ejava.examples.svc.docker.votes.services.ElectionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElectionListener {
    private final ElectionsService electionService;

    @EventListener
    public void newVote(NewVoteEvent newVoteEvent) {
        electionService.addVote(newVoteEvent.getVote());
    }
}
