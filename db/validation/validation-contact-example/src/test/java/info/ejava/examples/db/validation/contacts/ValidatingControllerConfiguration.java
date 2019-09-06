package info.ejava.examples.db.validation.contacts;

import info.ejava.examples.db.validation.contacts.controller.ContactsController;
import info.ejava.examples.db.validation.contacts.repo.ContactsRepository;
import info.ejava.examples.db.validation.contacts.svc.ContactsMapper;
import info.ejava.examples.db.validation.contacts.svc.InternalComponent;
import info.ejava.examples.db.validation.contacts.svc.PocService;
import info.ejava.examples.db.validation.contacts.svc.PocServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * This configuration sets up a validating controller and a non-validating service.
 * Any violations encountered are the result of the Web API validations.
 */
@Configuration
public class ValidatingControllerConfiguration {
    @Bean
    public PocService noValidationPocService(ContactsRepository contactsRepository, ContactsMapper contactsMapper, InternalComponent internalComponent) {
        return new PocServiceImpl(contactsRepository, contactsMapper, internalComponent);
    }

    @RestController
    @Validated //commenting this out will turn off validation for non-payload parameters
    public class ValidatingContactsController extends ContactsController {
        public ValidatingContactsController(PocService contactsService) {
            super(contactsService);
        }
    }
}
