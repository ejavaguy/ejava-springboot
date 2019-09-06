package info.ejava.examples.db.validation.contacts.svc;

import info.ejava.examples.db.validation.contacts.bo.PersonPOC;
import info.ejava.examples.db.validation.contacts.dto.PersonPocDTO;
import info.ejava.examples.db.validation.contacts.repo.ContactsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@RequiredArgsConstructor
public class PocServiceImpl implements PocService {
    private final ContactsRepository contactsRepository;
    private final ContactsMapper mapper;
    private final InternalComponent internalComponent;

    @Override
    public PersonPocDTO createPOC(PersonPocDTO personDTO) {
        PersonPOC bo = mapper.map(personDTO);
        contactsRepository.insert(bo);
        return mapper.map(bo);
    }

    @Override
    public Optional<PersonPocDTO> getPOC(String id) {
        PersonPOC person = contactsRepository.findById(id)
            .orElse(null);
        return Optional.ofNullable(mapper.map(person));
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
        Example<PersonPOC> probeBO = Example.of(mapper.map(probe));
        Page<PersonPOC> pocs = contactsRepository.findAll(probeBO, pageable);
        return pocs.map(p->mapper.map(p));
    }

    @Override
    public PersonPocDTO positiveOrZero(int value) {

        //obviously an error!!
        internalComponent.negativeOrZero(value);

        return new PersonPocDTO();
    }
}
