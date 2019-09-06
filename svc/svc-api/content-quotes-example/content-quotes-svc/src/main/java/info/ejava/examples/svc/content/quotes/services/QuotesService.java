package info.ejava.examples.svc.content.quotes.services;

import info.ejava.examples.svc.content.quotes.dto.QuoteDTO;
import info.ejava.examples.svc.content.quotes.dto.QuoteListDTO;

public interface QuotesService {
    QuoteDTO createQuote(QuoteDTO quote);
    void updateQuote(int id, QuoteDTO quote);
    void deleteQuote(int id);
    void deleteAllQuotes();

    boolean containsQuote(int id);
    QuoteDTO getQuote(int id);
    QuoteDTO randomQuote();

    QuoteListDTO getQuotes(int offset, int limit);
}
