package info.ejava.examples.db.validation.contacts;

import info.ejava.examples.db.validation.contacts.dto.PersonPocDTO;
import info.ejava.examples.db.validation.contacts.repo.ContactsRepository;
import info.ejava.examples.db.validation.contacts.svc.ContactsMapper;
import info.ejava.examples.db.validation.contacts.svc.PocService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Configuration
public class NullPOCConfiguration {

    @Validated
    public class NullPocService implements PocService {
        @Override
        public Optional<PersonPocDTO> getPOC(String id) {
            return Optional.empty();
        }

        @Override
        public PersonPocDTO createPOC(PersonPocDTO personDTO) {
            return null;
        }

        @Override
        public void updatePOC(String id, PersonPocDTO personDTO) {

        }

        @Override
        public long deletePOC(String id) {
            return 0;
        }

        @Override
        public long deleteAllPOCs() {
            return 0;
        }

        @Override
        public Page<PersonPocDTO> findPOCsMatchingAll(PersonPocDTO probe, Pageable pageable) {
            return null;
        }

        @Override
        public PersonPocDTO positiveOrZero(int value) {
            return null;
        }
    }

    @Bean
    public PocService nullPocService(ContactsRepository contactsRepository, ContactsMapper contactsMapper) {
        return new NullPocService();
    }
}
