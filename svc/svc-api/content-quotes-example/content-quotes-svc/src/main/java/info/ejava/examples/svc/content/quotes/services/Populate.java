package info.ejava.examples.svc.content.quotes.services;

import info.ejava.examples.svc.content.quotes.dto.QuoteDTOFactory;
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
    private final QuotesService quoteService;
    private QuoteDTOFactory quoteDTOFactory = new QuoteDTOFactory();

    @Override
    public void run(String... args) throws Exception {
        int count=20;
        log.info("populating {} quotes", count);
        quoteDTOFactory.listBuilder().make(count,count)
                .getQuotes()
                .stream()
                .forEach(contest -> quoteService.createQuote(contest));
    }
}
