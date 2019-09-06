package info.ejava.examples.db.validation.contacts.svc;

import info.ejava.examples.db.validation.contacts.ManualValidationConfiguration;
import info.ejava.examples.db.validation.contacts.NTestConfiguration;
import info.ejava.examples.db.validation.contacts.ValidatorConfiguration;
import info.ejava.examples.db.validation.contacts.dto.PersonPocDTO;
import info.ejava.examples.db.validation.contacts.dto.factories.PersonPocDTOFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;
import java.util.Collections;

import static info.ejava.examples.db.validation.contacts.dto.factories.PersonPocDTOFactory.oneUpId;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes={NTestConfiguration.class,
        ValidatorConfiguration.class,
        ManualValidationConfiguration.class,
})
@Tag("springboot")
@Slf4j
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ManualValidationNTest {
    @Autowired
    private PocService pocService;
    @Autowired
    private PersonPocDTOFactory pocFactory;

    @Test
    void poc_cannot_be_null() {
        //verify
        PersonPocDTO nullPOC = null;
        assertThatThrownBy(() -> pocService.createPOC(nullPOC))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("createPOC.person: must not be null");
    }

    @Test
    void name_cannot_be_null() {
        //verify
        PersonPocDTO annonymous = new PersonPocDTO();
        assertThatThrownBy(() -> pocService.createPOC(annonymous))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("createPOC.person: first and/or lastName must be supplied");
    }

    @Test
    void points_cannot_be_empty() {
        //verify
        PersonPocDTO noContactPoints = pocFactory.make()
                .withContactPoints(Collections.emptyList());
        assertThatThrownBy(() -> pocService.createPOC(noContactPoints))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("createPOC.person.contactPoints: must have at least one contact point");
    }

    @Test
    void points_cannot_have_a_null() {
        //verify
        PersonPocDTO noContactPoints = pocFactory.make()
                .withContactPoints(Collections.singletonList(null));
        assertThatThrownBy(() -> pocService.createPOC(noContactPoints))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("createPOC.person.contactPoints[0].<list element>: must not be null");
    }

    @Test
    void cannot_create_with_id_specified() {
        //verify
        PersonPocDTO pocWithId = pocFactory.make(oneUpId);
        assertThatThrownBy(() -> pocService.createPOC(pocWithId))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("createPOC.person.id: cannot be specified for create");
    }

    @Test
    void contactId_cannot_be_null() {
        //verify
        assertThatThrownBy(() -> pocService.getPOC(null))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessage("getPOC.id: must not be null");
    }
}
