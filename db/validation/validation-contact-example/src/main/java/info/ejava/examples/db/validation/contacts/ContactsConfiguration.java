package info.ejava.examples.db.validation.contacts;

import info.ejava.examples.db.validation.contacts.controller.ContactsController;
import info.ejava.examples.db.validation.contacts.repo.ContactsRepository;
import info.ejava.examples.db.validation.contacts.svc.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Configuration
public class ContactsConfiguration {
    @Bean
    public PocService pocService(ContactsRepository contactsRepository, ContactsMapper contactsMapper, InternalComponent internalComponent) {
        return new ValidatedPocServiceImpl(new PocServiceImpl(contactsRepository, contactsMapper, internalComponent));
    }

    @RestController
    public class ValidatedContactsController extends ContactsController {
        public ValidatedContactsController(PocService contactsService) {
            super(contactsService);
        }
    }
}
