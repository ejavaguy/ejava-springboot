package info.ejava.examples.db.validation.contacts.dto;

import info.ejava.examples.db.validation.contacts.dto.factories.PersonPocDTOFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.BDDAssertions.then;

@Slf4j
public class PersonPOCValidationTest {
    private static final Validator validator= Validation.buildDefaultValidatorFactory().getValidator();
    private static final PersonPocDTOFactory dtoFactory=new PersonPocDTOFactory();

    @Test
    void accepts_valid_person() {
        //given
        PersonPocDTO validPOC = dtoFactory.make();
        //when
        Set<ConstraintViolation<PersonPocDTO>> violations = validator.validate(validPOC);
        log.info("\n{}",details(violations));
        //then
        then(violations).hasSize(0);
   }

    @Test
    void accepts_null_lastname() {
        //given
        PersonPocDTO validPOC = dtoFactory.make()
                .withLastName(null);
        //when
        Set<ConstraintViolation<PersonPocDTO>> violations = validator.validate(validPOC);
        log.info("\n{}",details(violations));
        //then
        then(violations).hasSize(0);
    }

    @Test
    void accepts_null_firstname() {
        //given
        PersonPocDTO validPOC = dtoFactory.make()
                .withFirstName(null);
        //when
        Set<ConstraintViolation<PersonPocDTO>> violations = validator.validate(validPOC);
        log.info("\n{}",details(violations));
        //then
        then(violations).hasSize(0);
    }

    @Test
    void rejects_noname_person() {
        //given
        PersonPocDTO nonamePOC = dtoFactory.make()
                .withFirstName(null)
                .withLastName(null);
        //when
        Set<ConstraintViolation<PersonPocDTO>> violations = validator.validate(nonamePOC);
        log.info("\n{}",details(violations));
        //then
        then(violations).hasSize(1);
        then(errors(violations)).contains(
                ":first and/or lastName must be supplied"
        );
    }

    @Test
    void rejects_invalid_created_person() {
        //given
        PersonPocDTO invalidCreatePOC = dtoFactory.make(PersonPocDTOFactory.oneUpId)
                .withFirstName(null)
                .withLastName(null)
                .withDob(LocalDate.now().plusYears(1))
                .withContactPoints(Collections.emptyList());
        //when
        Set<ConstraintViolation<PersonPocDTO>> violations =
                validator.validate(invalidCreatePOC, PocValidationGroups.CreatePlusDefault.class);
        log.info("\n{}", StringUtils.join(details(violations), "\n"));
        //then
        then(errors(violations)).contains(
                "id:cannot be specified for create",
                "dob:must be a past date",
                ":first and/or lastName must be supplied",
                "contactPoints:must have at least one contact point"
        );
        then(violations).hasSize(4);
    }


    @Test
    void rejects_null_sequence_element() {
        //given
        PersonPocDTO invalidCreatePOC = dtoFactory.make()
                .withContactPoints(Collections.singletonList(null));
        //when
        Set<ConstraintViolation<PersonPocDTO>> violations =
                validator.validate(invalidCreatePOC, PocValidationGroups.CreatePlusDefault.class);
        log.info("\n{}", StringUtils.join(details(violations), "\n"));
        //then
        then(errors(violations)).contains(
                "contactPoints[0].<list element>:must not be null"
        );
        then(violations).hasSize(1);
    }


    private List<String> errors(Set<?> violations) {
        return violations.stream()
                .filter(v->v!=null)
                .map(v->(ConstraintViolation) v)
                .map(v -> v.getPropertyPath().toString() +
                        ":" +
                        v.getMessage())
                .collect(Collectors.toList());
    }
    private static String getInvalidValue(ConstraintViolation v) {
        if (v.getInvalidValue()==null) {
            return "(null)";
        } else {
            return (PersonPocDTO.class!=v.getInvalidValue().getClass() ?
                    v.getInvalidValue().toString() : "(person)");
        }
    }
    private List<String> details(Set<?> violations) {
        return violations.stream()
                .filter(v->v!=null)
                .map(v->(ConstraintViolation) v)
                .map(v -> v.getPropertyPath().toString() +
                        "=" + getInvalidValue(v) +
                        ":" + v.getMessage())
                .collect(Collectors.toList());
    }
}
