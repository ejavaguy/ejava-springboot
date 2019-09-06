package info.ejava.examples.db.validation.contacts.demo;

import info.ejava.examples.db.validation.contacts.ValidatorConfiguration;
import info.ejava.examples.db.validation.contacts.dto.ContactPointDTO;
import info.ejava.examples.db.validation.contacts.dto.PersonPocDTO;
import info.ejava.examples.db.validation.contacts.dto.factories.ContactDTOFactory;
import info.ejava.examples.db.validation.contacts.dto.PocValidationGroups;
import info.ejava.examples.db.validation.contacts.dto.factories.PersonPocDTOFactory;
import info.ejava.examples.db.validation.contacts.svc.PocService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.BDDAssertions.then;

@ExtendWith(MockitoExtension.class)
@Slf4j
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class GroupTest {
    private ContactDTOFactory contactDTOFactory=new ContactDTOFactory();
    private PersonPocDTOFactory pocDTOFactory = new PersonPocDTOFactory();
    @Mock
    private PocService pocServiceMock;
    private Validator validator=new ValidatorConfiguration().validator();

    @Test
    void invalid_create_state_detected() {
        //given -
        PersonPocDTO invalidForCreate = pocDTOFactory.make(PersonPocDTOFactory.oneUpId);
        String expectedError="id:cannot be specified for create";
        //when
        Set<ConstraintViolation<PersonPocDTO>> violations = validator.validate(invalidForCreate);
        //then
        log.info("\n{}", StringUtils.join(details(violations)));
        then(violations).hasSize(0);
        then(errors(violations)).doesNotContain(expectedError);

        //when
        violations = validator.validate(invalidForCreate, PocValidationGroups.CreatePlusDefault.class);
        //then
        log.info("\n{}", StringUtils.join(details(violations),"\n"));
        then(violations).hasSize(1);
        then(errors(violations)).contains(expectedError);
    }



    @Test
    void conditions_in_sequence() {
        //given
        ContactPointDTO invalidContact = contactDTOFactory.make(
                ContactDTOFactory.noName,
                ContactDTOFactory.emailToShort);
        String nameError="name:must not be null"; //Default Group
        String sizeError="email:size must be between 7 and 40"; //Simple Group
        String formatError="email:must be a well-formed email address"; //DetailedOnly Group

        //when - validating against all groups
        Set<ConstraintViolation<ContactPointDTO>> violations = validator.validate(invalidContact,
                PocValidationGroups.SimplePlusDefault.class, PocValidationGroups.DetailedOnly.class);
        //then - all groups will have their violations reported
        log.info("\n{}", StringUtils.join(details(violations)));
        then(errors(violations)).contains(nameError, sizeError, formatError).hasSize(3);

        //when - validating using a @GroupSequence
        violations = validator.validate(invalidContact, PocValidationGroups.DetailOrder.class);
        //then - validation stops once a group produces a violation
        log.info("\n{}", StringUtils.join(details(violations),"\n"));
        then(errors(violations)).contains(nameError, sizeError).hasSize(2);
    }



    private List<String> errors(Set<?> violations) {
        return violations.stream()
                .map(v->(ConstraintViolation) v)
                .map(v -> v.getPropertyPath().toString() +
                        ":" +
                        v.getMessage())
                .collect(Collectors.toList());
    }
    private List<String> details(Set<?> violations) {
        return violations.stream()
                .map(v->(ConstraintViolation) v)
                .map(v -> v.getPropertyPath().toString() +
                        "=" + v.getInvalidValue() +
                        ":" +
                        v.getMessage())
                .collect(Collectors.toList());
    }
}
