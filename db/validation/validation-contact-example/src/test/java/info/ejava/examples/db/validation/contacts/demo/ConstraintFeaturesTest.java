package info.ejava.examples.db.validation.contacts.demo;

import info.ejava.examples.db.validation.contacts.NTestConfiguration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.BDDAssertions.*;

@SpringBootTest(classes = {NTestConfiguration.class})
@Tag("springboot")
@Slf4j
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ConstraintFeaturesTest {
    @Autowired
    private Validator validator;

    @Builder
    @AllArgsConstructor
    private static class AClass {
        @Pattern(regexp = "^[A-Za-z]+$")
        private String aPattern;
        @Pattern(regexp = "^[A-Za-z]+$", message = "must have all letters")
        private String allLetters;
        @Min(5)
        @Max(10)
        private Integer aNumber;
        @Max(value = 50)
        @Max(value = 100)
        private Integer bNumber;
    };

    @Test
    void programmatic_validation_valid() {
        //given
        AClass valid = AClass.builder().aPattern("ABCefg").build();
        //when
        Set<ConstraintViolation<AClass>> violations = validator.validate(valid);
        for (ConstraintViolation v: violations) {
            log.info("field name={}, value={}, violated={}", v.getPropertyPath(), v.getInvalidValue(), v.getMessage());
        }
        //then
        then(violations).isEmpty();
    }

    @Test
    void programmatic_validation_invalid() {
        //given
        AClass invalid = AClass.builder().aPattern("ABCefg123").build();
        //when
        Set<ConstraintViolation<AClass>> violations = validator.validate(invalid);
        for (ConstraintViolation v: violations) {
            log.info("field name={}, value={}, violated={}", v.getPropertyPath(), v.getInvalidValue(), v.getMessage());
        }
        //then
        then(violations).isNotEmpty();
    }

    @Test
    void alternate_message() {
        //given
        AClass object = AClass.builder().allLetters("ABCefg123").build();
        //when
        Set<ConstraintViolation<AClass>> violations = validate(object);
        //then
        then(violations).hasSize(1);
        ConstraintViolation<AClass> violation = getViolationByPath(violations, "allLetters").get(0);
        then(violation.getMessage()).isEqualTo("must have all letters");
    }

    @Test
    void validates_min() {
        //given
        var object = AClass.builder().aNumber(1).build();
        //when
        Set<ConstraintViolation<AClass>> violations = validate(object);
        //then
        then(violations).hasSize(1);
        ConstraintViolation<AClass> violation = violations.iterator().next();
        then(violation.getPropertyPath().toString()).isEqualTo("aNumber");
        then(violation.getMessage()).isEqualTo("must be greater than or equal to 5");
    }

    @Test
    void validates_max() {
        //given
        var object = AClass.builder().aNumber(20).build();
        //when
        Set<ConstraintViolation<AClass>> violations = validate(object);
        //then
        then(violations).hasSize(1);
        ConstraintViolation<AClass> violation = violations.iterator().next();
        then(violation.getPropertyPath().toString()).isEqualTo("aNumber");
        then(violation.getMessage()).isEqualTo("must be less than or equal to 10");
    }

    @Test
    void reports_multiple_violations() {
        //given
        var object = AClass.builder().aNumber(20).bNumber(1000).build();
        //when
        Set<ConstraintViolation<AClass>> violations = validate(object);
        //then
        then(violations).hasSize(3);
        then(getViolationByPath(violations, "aNumber")).hasSize(1);
        then(getViolationByPath(violations, "bNumber")).hasSize(2);
    }

    @Builder
    @AllArgsConstructor
    private static class Wrapper {
        private AClass notValidated;
        @Valid
        private AClass validated;
    }
    AClass invalid = AClass.builder().aNumber(1).build();

    @Test
    void contained_object_not_validated_by_default() {
        //given
        Wrapper wrapper = Wrapper.builder().notValidated(invalid).build();
        //when
        Set<ConstraintViolation<Wrapper>> violations = validate(wrapper);
        //then
        then(violations).isEmpty();
    }
    @Test
    void contained_object_validated() {
        //given
        Wrapper wrapper = Wrapper.builder().validated(invalid).build();
        //when
        Set<ConstraintViolation<Wrapper>> violations = validate(wrapper);
        //then
        then(violations).hasSize(1);
        then(getViolationByPath(violations, "validated.aNumber")).hasSize(1);
    }

    private <T> Set<ConstraintViolation<T>> validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        for (ConstraintViolation v: violations) {
            log.info("field name={}, value={}, violated={}", v.getPropertyPath(), v.getInvalidValue(), v.getMessage());
        }
        return violations;
    }

    private <T> List<ConstraintViolation<T>> getViolationByPath(Set<ConstraintViolation<T>> violations, String path) {
        return violations.stream()
                .filter(v->path.equals(v.getPropertyPath().toString()))
                .collect(Collectors.toList());
    }
}
