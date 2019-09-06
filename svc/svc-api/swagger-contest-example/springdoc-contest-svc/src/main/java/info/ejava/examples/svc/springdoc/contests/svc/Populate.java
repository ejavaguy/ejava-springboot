package info.ejava.examples.svc.springdoc.contests.svc;

import info.ejava.examples.svc.springdoc.contests.dto.ContestDTOFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name="test", havingValue = "true", matchIfMissing = true)
@Slf4j
public class Populate implements CommandLineRunner {
    private final ContestService contestService;
    private ContestDTOFactory contestDTOFactory = new ContestDTOFactory();

    @Override
    public void run(String... args) throws Exception {
        int count=20;
        log.info("populating {} contests", count);
        contestDTOFactory.listBuilder().make(count, count)
                .getContests()
                .stream()
                .forEach(contest -> contestService.createContest(contest));
    }
}
