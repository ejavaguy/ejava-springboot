package info.ejava.examples.db.validation.contacts.controller;


import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava.examples.common.web.paging.PageableDTO;
import info.ejava.examples.db.validation.contacts.dto.PersonPocDTO;
import info.ejava.examples.db.validation.contacts.dto.PersonsPageDTO;
import info.ejava.examples.db.validation.contacts.dto.PocValidationGroups;
import info.ejava.examples.db.validation.contacts.svc.PocService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
//@Validated - will be conditionally applied by @Bean factory in this example
public class ContactsController {
    public static final String CONTACTS_PATH ="api/contacts";
    public static final String CONTACT_PATH = CONTACTS_PATH + "/{id}";
    public static final String RANDOM_CONTACT_PATH = CONTACTS_PATH + "/random";
    public static final String EXAMPLE_CONTACTS_PATH = CONTACTS_PATH + "/example";
    public static final String POSITIVE_OR_ZERO_PATH = CONTACTS_PATH + "/positiveOrZero";

    private final PocService contactsService;

    @RequestMapping(path=CONTACTS_PATH,
            method= RequestMethod.POST,
            consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Validated(PocValidationGroups.CreatePlusDefault.class) //directs vanilla AOP what to validate against
    public ResponseEntity<PersonPocDTO> createPOC(
            @RequestBody
            /*
            Validation of the @RequestBody is performed by the HTTP/MVC layer independent
            of vanilla @Validated class-level annotation rules. Any addition of @Valid
            or @Validated here will cause the incoming payload to be validated and
            an independent MethodArgumentNotValidException throws for the exception handler
            to address. We will want to map that to a 422/UnprocessableEntity status.
             */
            //@Valid //only supports Default group; use @Validated to customize group(s)
            @Validated(PocValidationGroups.CreatePlusDefault.class) //supports naming group(s)
            PersonPocDTO personDTO) {

        PersonPocDTO result = contactsService.createPOC(personDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .replacePath(CONTACT_PATH)
                .build(result.getId());
        ResponseEntity<PersonPocDTO> response = ResponseEntity.created(uri).body(result);
        return response;
    }

    @RequestMapping(path= CONTACT_PATH,
            method=RequestMethod.GET,
            produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PersonPocDTO> getPOC(
            @PathVariable(name="id")
            /*
            PathVariable validation is controlled by normal class-level @Validated annotation
            rules that use vanilla AOP intercept. A violation here causes a ConstraintViolationException
            to be thrown.
             */
            @Pattern(regexp = "[0-9]+", message = "must be a number")
                    String id) {

        PersonPocDTO result = contactsService.getPOC(id)
                .orElseThrow(()->new ClientErrorException.NotFoundException("POC[%s] not found",id));

        ResponseEntity<PersonPocDTO> response = ResponseEntity.ok(result);
        return response;
    }

    @RequestMapping(path= CONTACT_PATH,
            method=RequestMethod.PUT,
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> updatePOC(
            @PathVariable(value = "id", required = true) String id,
            @RequestBody @Valid PersonPocDTO personDTO) {

        contactsService.updatePOC(id, personDTO);

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

    @RequestMapping(path= CONTACT_PATH,
            method=RequestMethod.DELETE)
    /*
    A @Valididated on the method causes non-RequestBody parameters to be validated
    according to the groups specified. Otherwise, just the default group is used.
    If we comment out the group here (changing it to default to default), no validation
    will be performed because "id" is assigned only to that group.
     */
    @Validated(PocValidationGroups.DetailedOnly.class)
    public ResponseEntity<Void> deletePOC(
            @PathVariable(name="id", required = true)
            /*
            This PathVariable will get validated when the Detailed group is activated.
             */
            @Pattern(regexp = "[0-9]+", message = "must be a number", groups = PocValidationGroups.DetailedOnly.class)
                    String id) {

        contactsService.deletePOC(id);

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

    @RequestMapping(path= CONTACTS_PATH,
            method=RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllPOCs() {

        contactsService.deleteAllPOCs();

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

    @RequestMapping(path= EXAMPLE_CONTACTS_PATH,
            method=RequestMethod.POST,
            consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<PersonsPageDTO> findPocsByExample(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false)
            @PositiveOrZero
            Integer pageNumber,

            @RequestParam(value = "pageSize", required = false)
            @Positive
            Integer pageSize,

            @RequestParam(value = "sort", required = false) String sortString,
            @RequestBody PersonPocDTO probe) {

        Pageable pageable = PageableDTO.of(pageNumber, pageSize, sortString).toPageable();

        Page<PersonPocDTO> result= contactsService.findPOCsMatchingAll(probe, pageable);

        PersonsPageDTO resultDTO = new PersonsPageDTO(result);
        ResponseEntity<PersonsPageDTO> response = ResponseEntity.ok(resultDTO);
        return response;
    }

    /**
     * This endpoint will always reach an internal validated exchange that causes a violation.
     * It is here to show the potential ambiguity of a client input error versus an
     * internal error.
     */
    @RequestMapping(path = POSITIVE_OR_ZERO_PATH,
    method=RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> positive(
            @PositiveOrZero
            @RequestParam(name = "value") int value) {
        PersonPocDTO resultDTO = contactsService.positiveOrZero(value);
        ResponseEntity<?> response = ResponseEntity.ok(resultDTO);
        return response;
    }
}
