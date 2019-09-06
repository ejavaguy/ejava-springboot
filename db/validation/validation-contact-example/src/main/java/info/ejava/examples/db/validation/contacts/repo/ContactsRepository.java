package info.ejava.examples.db.validation.contacts.repo;

import info.ejava.examples.db.validation.contacts.bo.PersonPOC;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContactsRepository extends MongoRepository<PersonPOC, String> {
}
