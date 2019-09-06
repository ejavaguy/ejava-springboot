package info.ejava.examples.db.validation.contacts;

import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava.examples.common.exceptions.ServerErrorException;
import info.ejava.examples.db.validation.contacts.bo.PersonPOC;
import info.ejava.examples.db.validation.contacts.dto.ContactPointDTO;
import info.ejava.examples.db.validation.contacts.dto.PersonPocDTO;
import info.ejava.examples.db.validation.contacts.repo.ContactsRepository;
import info.ejava.examples.db.validation.contacts.svc.ContactsMapper;
import info.ejava.examples.db.validation.contacts.svc.PocService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static info.ejava.examples.common.exceptions.ClientErrorException.*;
import static info.ejava.examples.common.exceptions.ServerErrorException.*;

@Configuration
public class ManualValidationConfiguration {

    @Validated
    @RequiredArgsConstructor
    public class NullPocService implements PocService {
        private final ContactsRepository contactsRepository;
        private final ContactsMapper mapper;

        @Override
        public Optional<PersonPocDTO> getPOC(String id) {
            return Optional.empty();
        }

        @Override
        public PersonPocDTO createPOC(PersonPocDTO personDTO) {
            if (personDTO==null) {
                throw new BadRequestException("createPOC.person: must not be null");
            } else if (StringUtils.isNotBlank(personDTO.getId())) {
                throw new InvalidInputException("createPOC.person.id: must be null");
            } else if (StringUtils.isBlank(personDTO.getFirstName()) && StringUtils.isBlank(personDTO.getFirstName())) {
                throw new InvalidInputException("createPOC.person: first and/or lastName must be supplied");
            } else if (CollectionUtils.isEmpty(personDTO.getContactPoints())) {
                throw new InvalidInputException("createPOC.person.contactPoints: must have at least one contact point");
            }
            IntStream.range(0,personDTO.getContactPoints().size()).forEach(idx->{
                if (personDTO.getContactPoints().get(idx) == null) {
                    throw new InvalidInputException("createPOC.person.contactPoints[%d].<list element>: must not be null", idx);
                }
            });

            PersonPOC bo = mapper.map(personDTO);
            contactsRepository.insert(bo);
            PersonPocDTO resultPOC = mapper.map(bo);

            if (resultPOC==null) {
                throw new InternalErrorException("createPOC.<return value>: must not be null");
            }
            return personDTO;
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
        return new NullPocService(contactsRepository, contactsMapper);
    }
}
