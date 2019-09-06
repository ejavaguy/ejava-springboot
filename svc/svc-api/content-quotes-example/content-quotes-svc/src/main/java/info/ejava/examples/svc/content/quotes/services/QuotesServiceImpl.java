package info.ejava.examples.svc.content.quotes.services;

import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava.examples.common.exceptions.ClientErrorException.BadRequestException;
import info.ejava.examples.common.exceptions.ClientErrorException.NotFoundException;
import info.ejava.examples.svc.content.quotes.dto.QuoteDTO;
import info.ejava.examples.svc.content.quotes.dto.QuoteListDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static info.ejava.examples.common.exceptions.ServerErrorException.InternalErrorException;

@Service
@Slf4j
public class QuotesServiceImpl implements QuotesService {
    private final AtomicInteger nextId = new AtomicInteger(1);
    private final Map<Integer, QuoteDTO> quotes;

    public QuotesServiceImpl(Map<Integer, QuoteDTO> quoteMap) {
        this.quotes = quoteMap;
    }

    @Override
    public QuoteDTO createQuote(QuoteDTO quote) {
        validate(quote);
        quote.setId(nextId.getAndAdd(1));
        quotes.put(quote.getId(), quote);
        return quote;
    }

    @Override
    public void updateQuote(int id, QuoteDTO quote) {
        validate(quote);
        if (quotes.containsKey(id)) {
            quote.setId(id);
            quotes.put(quote.getId(), quote);
        } else {
            throw new NotFoundException("quote[%d] not found", id);
        }
    }

    protected void validate(QuoteDTO quote) {
        if (StringUtils.isEmpty(quote.getText())) {
            throw new ClientErrorException.InvalidInputException("quote is missing required text");
        }
    }

    @Override
    public void deleteQuote(int id) {
        quotes.remove(id);
    }

    @Override
    public void deleteAllQuotes() {
        quotes.clear();
    }

    @Override
    public boolean containsQuote(int id) {
        return quotes.containsKey(id);
    }

    @Override
    public QuoteDTO getQuote(int id) {
        QuoteDTO quote = quotes.get(id);
        if (quote!=null) {
            return quote;
        } else {
            throw new NotFoundException("quote[%d] not found", id);
        }
    }

    @Override
    public QuoteDTO randomQuote() {
        if (quotes.isEmpty()) {
            throw new NotFoundException("we have no quotes");
        }

        int randomIndex = new SecureRandom().nextInt(quotes.size());
        int index=0;
        for (QuoteDTO q: quotes.values()) {
            if (index++ == randomIndex) {
                return q;
            }
        }

        throw new InternalErrorException("error trying to locate random quote[%d]", randomIndex);
    }

    @Override
    public QuoteListDTO getQuotes(int offset, int limit) {
        validatePaging(offset, limit);

        List<QuoteDTO> responses = new ArrayList<>(limit);
        Iterator<QuoteDTO> itr = quotes.values().iterator();
        for (int i=0; itr.hasNext() && (limit==0 || responses.size()<limit); i++) {
            QuoteDTO q = itr.next();
            if (i>=offset) {
                responses.add(q);
            }
        }

        return QuoteListDTO.builder()
                .offset(offset)
                .limit(limit)
                .total(quotes.size())
                .quotes(responses)
                .build();
    }

    public void validatePaging(int offset, int limit) {
        if (offset<0 || limit<0) {
            throw new BadRequestException("offset[%d] or limit[%d] must be >=0", offset, limit);
        }
    }
}
