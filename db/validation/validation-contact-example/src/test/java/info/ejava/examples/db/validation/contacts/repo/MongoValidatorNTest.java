package info.ejava.examples.db.validation.contacts.repo;

import info.ejava.examples.db.validation.contacts.MongoConfiguration;
import info.ejava.examples.db.validation.contacts.NTestConfiguration;
import info.ejava.examples.db.validation.contacts.ValidatorConfiguration;
import info.ejava.examples.db.validation.contacts.bo.PersonPOC;
import info.ejava.examples.db.validation.contacts.dto.factories.PersonPocDTOFactory;
import info.ejava.examples.db.validation.contacts.svc.ContactsMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes={
        NTestConfiguration.class,
        ValidatorConfiguration.class,
        MongoConfiguration.class
})
@Tag("springboot")
@Slf4j
public class MongoValidatorNTest {
    @Autowired
    private PersonPocDTOFactory pocDTOFactory;
    @Autowired
    private ContactsMapper mapper;
    @Autowired
    private ContactsRepository contactsRepository;

    @Test
    void validate_save() {
        //given
        PersonPOC noDobPOC = mapper.map(pocDTOFactory.make().withDob(null));
        //when
        assertThatThrownBy(() -> contactsRepository.save(noDobPOC))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("dob: must not be null");
    }
}
