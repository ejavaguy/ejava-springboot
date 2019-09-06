package info.ejava.examples.svc.content.quotes.controllers;

import info.ejava.examples.svc.content.quotes.dto.QuoteDTO;
import info.ejava.examples.svc.content.quotes.dto.QuoteListDTO;
import info.ejava.examples.svc.content.quotes.services.QuotesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static info.ejava.examples.svc.content.quotes.api.QuotesAPI.*;

@RestController
@Slf4j
public class QuotesController {
    private final QuotesService quotesService;

    public QuotesController(QuotesService quotesService) {
        this.quotesService = quotesService;
    }

    /**
     * This method provides two example method signatures. Use the @RequestBody form when
     * headers are of no interest. Use RequestEntity&lt;Quote&gt; form when headers are
     * of interest
     */
    @RequestMapping(path=QUOTES_PATH,
            method= RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
//    public ResponseEntity<QuoteDTO> createQuote(@RequestBody QuoteDTO quote) {
    public ResponseEntity<QuoteDTO> createQuote(RequestEntity<QuoteDTO> request) {
        QuoteDTO quote = request.getBody();
        log.info("CONTENT_TYPE={}", request.getHeaders().get(HttpHeaders.CONTENT_TYPE));
        log.info("ACCEPT={}", request.getHeaders().get(HttpHeaders.ACCEPT));
        QuoteDTO result = quotesService.createQuote(quote);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .replacePath(QUOTE_PATH)
                .build(result.getId());
        ResponseEntity<QuoteDTO> response = ResponseEntity.created(uri)
                .body(result);
        return response;
    }

    @RequestMapping(path=QUOTES_PATH,
        method=RequestMethod.GET,
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<QuoteListDTO> getQuotes(
            @RequestParam(name="offset", defaultValue = "0") int offset,
            @RequestParam(name="limit", defaultValue = "0") int limit) {
        QuoteListDTO quotes = quotesService.getQuotes(offset, limit);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        ResponseEntity<QuoteListDTO> response = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_LOCATION, uri.toString())
                .body(quotes);
        return response;
    }

    @RequestMapping(path=QUOTE_PATH,
            method= RequestMethod.PUT,
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> updateQuote(@PathVariable("id") int id, @RequestBody QuoteDTO quote) {

        quotesService.updateQuote(id, quote);

        ResponseEntity<Void> response = ResponseEntity.ok().build();
        return response;
    }

    @RequestMapping(path=QUOTE_PATH,
            method= RequestMethod.HEAD)
    public ResponseEntity<Void> containsQuote(@PathVariable("id") int id) {
        boolean exists = quotesService.containsQuote(id);

        ResponseEntity<Void> response = exists ?
                ResponseEntity.ok().build() :
                ResponseEntity.notFound().build();
        return response;
    }


    @RequestMapping(path=QUOTE_PATH,
            method= RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<QuoteDTO> getQuote(@PathVariable("id") int id) {
        QuoteDTO quote = quotesService.getQuote(id);

        ResponseEntity response = ResponseEntity.ok(quote);
        return response;
    }

    @RequestMapping(path=RANDOM_QUOTE_PATH,
            method= RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<QuoteDTO> randomQuote() {
        QuoteDTO quote = quotesService.randomQuote();

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath(QUOTE_PATH).build(quote.getId());
        ResponseEntity response = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_LOCATION, uri.toString())
                .body(quote);
        return response;
    }

    @RequestMapping(path=QUOTE_PATH,
            method= RequestMethod.DELETE)
    public ResponseEntity<Void> deleteQuote(@PathVariable("id") int id) {
        quotesService.deleteQuote(id);

        ResponseEntity response = ResponseEntity.noContent().build();
        return response;
    }

    @RequestMapping(path=QUOTES_PATH,
            method= RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllQuotes() {
        quotesService.deleteAllQuotes();
        ResponseEntity response = ResponseEntity.noContent().build();
        return response;
    }
}
