package info.ejava.examples.db.validation.contacts;

import info.ejava.examples.db.validation.contacts.controller.ContactsController;
import info.ejava.examples.db.validation.contacts.dto.PersonPocDTO;
import info.ejava.examples.db.validation.contacts.repo.ContactsRepository;
import info.ejava.examples.db.validation.contacts.svc.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This configuration sets up a non-validating controller and a validating service.
 * The service is the first to check and detect all violations.
 */
@Configuration
public class ValidatingServiceConfiguration {
    @Bean
    public PocService validatingPocService(ContactsRepository contactsRepository, ContactsMapper contactsMapper, InternalComponent internalComponent) {
        return new ValidatedPocServiceImpl(new PocServiceImpl(contactsRepository, contactsMapper, internalComponent));
    }

    @RestController
    //validation will not be active in the controller
    public class NonValidatingContactsController extends ContactsController {
        public NonValidatingContactsController(PocService contactsService) {
            super(contactsService);
        }

        /*
        This override will hide the @Validated of the parent Controller method so that
        all validation is handled by the service for this method.
         */
        @Override
        public ResponseEntity<PersonPocDTO> createPOC(
                @RequestBody PersonPocDTO personDTO) {
            return super.createPOC(personDTO);
        }
    }
}
