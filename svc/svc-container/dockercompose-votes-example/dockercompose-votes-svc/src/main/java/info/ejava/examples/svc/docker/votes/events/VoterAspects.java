package info.ejava.examples.svc.docker.votes.events;

import info.ejava.examples.svc.docker.votes.dto.VoteDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class VoterAspects {
    private final VoterJMS votePublisher;

    @Pointcut("within(info.ejava.examples.svc.docker.votes.services.VoterService+)")
    public void voterService(){}

    @Pointcut("execution(*..VoteDTO castVote(..))")
    public void castVote(){}

    @AfterReturning(value = "voterService() && castVote()", returning = "vote")
    public void afterVoteCast(VoteDTO vote) {
        try {
            log.info("vote cast {}", vote);
            votePublisher.publish(vote);
            log.info("vote published {}", vote);
        } catch (IOException ex) {
            log.warn("error publishing vote {}", vote, ex);
        }
    }
}
