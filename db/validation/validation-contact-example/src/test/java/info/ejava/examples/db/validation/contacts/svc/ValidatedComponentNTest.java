package info.ejava.examples.db.validation.contacts.svc;

import info.ejava.examples.db.validation.contacts.NTestConfiguration;
import info.ejava.examples.db.validation.contacts.ValidatorConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.BDDAssertions;
import org.assertj.core.api.ThrowableAssertAlternative;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes={NTestConfiguration.class, ValidatorConfiguration.class})
@Tag("springboot")
@Slf4j
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ValidatedComponentNTest {
    @Autowired
    private ValidatedComponent component;

    @Test
    void validate_parameter_valid() {
        assertThatNoException().isThrownBy(()->component.aCall(10));
    }

    @Test
    void validate_parameter_invalid() {
        //when
        ConstraintViolationException ex = catchThrowableOfType(
                () -> component.aCall(1),
                ConstraintViolationException.class);
        log(ex.getConstraintViolations());
        //then
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String errorMsgs = violations.stream()
                .map(v->v.getPropertyPath().toString() +":" + v.getMessage())
                .collect(Collectors.joining("\n"));
        then(errorMsgs).isEqualTo("aCall.mustBeGE5:must be greater than or equal to 5");
        then(ex.getConstraintViolations()).hasSize(1);
    }

    private Set<ConstraintViolation<?>> log(Set<ConstraintViolation<?>> violations) {
        for (ConstraintViolation v: violations) {
            log.info("field name={}, value={}, violated={}", v.getPropertyPath(), v.getInvalidValue(), v.getMessage());
        }
        return violations;
    }

}
