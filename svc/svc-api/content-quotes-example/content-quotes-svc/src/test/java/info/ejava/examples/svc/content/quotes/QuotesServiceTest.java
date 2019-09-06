package info.ejava.examples.svc.content.quotes;

import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava.examples.common.exceptions.ClientErrorException.BadRequestException;
import info.ejava.examples.common.exceptions.ClientErrorException.NotFoundException;
import info.ejava.examples.svc.content.quotes.dto.QuoteDTO;
import info.ejava.examples.svc.content.quotes.dto.QuoteDTOFactory;
import info.ejava.examples.svc.content.quotes.dto.QuoteListDTO;
import info.ejava.examples.svc.content.quotes.services.QuotesService;
import info.ejava.examples.svc.content.quotes.services.QuotesServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static info.ejava.examples.common.exceptions.ClientErrorException.InvalidInputException;
import static info.ejava.examples.svc.content.quotes.dto.QuoteDTOFactory.oneUpId;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class QuotesServiceTest {
    QuotesService quotesService;

    @Mock
    private Map<Integer, QuoteDTO> quoteMap;

    private QuoteDTOFactory quotesFactory = new QuoteDTOFactory();

    @BeforeEach
    public void init() {
        quotesService=new QuotesServiceImpl(quoteMap);
    }

    @Test
    public void add_quote_accepted() {
        //given - valid quote
        QuoteDTO validQuote = quotesFactory.make();

        //when - added to the service
        QuoteDTO createdQuote = quotesService.createQuote(validQuote);

        //then - an identifier was created
        then(createdQuote.getId()).isPositive();
        log.debug("{}", createdQuote);
    }

    @Test
    public void update_existing_quote() {
        //given - an existing quote
        QuoteDTO existingQuote = quotesFactory.make(oneUpId);
        QuoteDTO updatedQuote = quotesFactory.make();
        given(quoteMap.containsKey(existingQuote.getId())).willReturn(Boolean.TRUE);

        //when - updating existing quote
        quotesService.updateQuote(existingQuote.getId(), updatedQuote);

        //then - no exection was thrown and ...
        BDDMockito.then(quoteMap).should().containsKey(existingQuote.getId());
        BDDMockito.then(quoteMap).should().put(existingQuote.getId(), updatedQuote);
    }

    @Test
    public void get_quote() {
        //given - an existing quote
        QuoteDTO existingQuote = quotesFactory.make(oneUpId);
        int requestId = existingQuote.getId();
        given(quoteMap.get(requestId)).willReturn(existingQuote);

        //when - requesting quote by id
        QuoteDTO returnedQuote = quotesService.getQuote(requestId);

        //then
        BDDMockito.then(quoteMap).should(times(1)).get(requestId);
        then(returnedQuote).isEqualTo(existingQuote);
    }

    @Test
    public void get_random_quote() {
        //given - many quotes
        given(quoteMap.size()).willReturn(100);
        given(quoteMap.values()).willReturn(quotesFactory.listBuilder().quotes(100, 100, oneUpId));

        //then
        QuoteDTO returnedQuote = quotesService.randomQuote();

        //then
        BDDMockito.then(quoteMap).should(times(1)).size();
        BDDMockito.then(quoteMap).should(times(1)).values();
        then(returnedQuote).isNotNull();
        log.debug("random quote:{}", returnedQuote);
    }

    @Test
    public void remove_quote() {
        //given
        QuoteDTO existingQuote = quotesFactory.make(oneUpId);
        int requestId = existingQuote.getId();
        given(quoteMap.remove(requestId)).willReturn(existingQuote);

        //when - requested to remove
        quotesService.deleteQuote(requestId);

        //then
        BDDMockito.then(quoteMap).should(times(1)).remove(requestId);
    }


    @Test
    public void remove_all_quotes() {
        //when - requested to remove all quotes
        quotesService.deleteAllQuotes();

        //then - map should be cleared
        BDDMockito.then(quoteMap).should(times(1)).clear();
    }

    @Test
    public void remove_unknown_quote() {
        //given
        int requestId = 13;
        given(quoteMap.remove(requestId)).willReturn(null);

        //when - requested to remove, will not report that does not exist
        quotesService.deleteQuote(requestId);

        //then
        BDDMockito.then(quoteMap).should(times(1)).remove(requestId);
    }

    @Test
    public void get_unknown_quote() {
        //given - no quotes and an unknown quoteId
        int unknownId=13;

        //when - requesting quote by id
        assertThatThrownBy(()->quotesService.getQuote(unknownId))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(String.format("quote[%d]", unknownId));

        //then
        BDDMockito.then(quoteMap).should(times(1)).get(unknownId);
    }

    @Test
    public void update_unknown_quote() {
        //given - an existing quote
        int unknownId=13;
        QuoteDTO updatedQuote = quotesFactory.make();

        //verify - that updating existing quote
        assertThatThrownBy(()->quotesService.updateQuote(unknownId, updatedQuote))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(String.format("quote[%d]", unknownId));

        //then - exection was thrown and ...
        verify(quoteMap, times(1)).containsKey(unknownId);
    }


    @Test
    public void update_known_quote_with_bad_quote() {
        //given - an existing quote
        int knownId = 22;
        QuoteDTO badQuoteMissingText = new QuoteDTO();

        //verify - that when updating quote
        assertThatThrownBy(()->quotesService.updateQuote(knownId, badQuoteMissingText))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining(String.format("missing required text", knownId));

        //then - exection was thrown and ...
        verify(quoteMap, times(0)).containsKey(knownId);
    }

    @Test
    public void add_bad_quote_rejected() {
        //given
        QuoteDTO badQuoteMissingText = new QuoteDTO();

        //verify
        assertThatThrownBy(()->quotesService.createQuote(badQuoteMissingText))
                .isInstanceOf(ClientErrorException.class)
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("text");
    }

    @ParameterizedTest
    @CsvSource({"-1,10", "10,-5"})
    public void get_invalid_offset_limit(int offset, int limit) {
        //verify
        assertThatThrownBy(()->quotesService.getQuotes(offset, limit))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("offset[%d]", offset)
                .hasMessageContaining("limit[%d]", limit);
    }

    @Test
    public void get_empty_quotes() {
        //given we have no quotes

        //when - asked for amounts we do not have
        QuoteListDTO response = quotesService.getQuotes(0, 100);
        log.debug("{}", response);

        //then - the response will be empty
        then(response.getCount()).isEqualTo(0);
        //and descriptive attributes filed in
        then(response.getOffset()).isEqualTo(0);
        then(response.getLimit()).isEqualTo(100);
        then(response.getTotal()).isEqualTo(0);
    }

    @Test
    public void get_many_quotes() {
        //given many quotes
        given(quoteMap.size()).willReturn(100);
        List<QuoteDTO> quotes = quotesFactory.listBuilder().quotes(100, 100, oneUpId);
        given(quoteMap.values()).willReturn(quotes);

        //when asking for a page of quotes
        QuoteListDTO response = quotesService.getQuotes(10, 10);

        //then - page of results returned
        then(response.getCount()).isEqualTo(10);
        then(response.getQuotes().get(0)).isEqualTo(quotes.get(10));
        then(response.getQuotes().get(9)).isEqualTo(quotes.get(19));

        //and descriptive attributes filed in
        then(response.getOffset()).isEqualTo(10);
        then(response.getLimit()).isEqualTo(10);
        then(response.getTotal()).isEqualTo(100);
    }
}
