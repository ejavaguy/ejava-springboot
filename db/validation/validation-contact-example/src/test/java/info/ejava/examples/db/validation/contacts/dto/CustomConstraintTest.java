package info.ejava.examples.db.validation.contacts.dto;

import info.ejava.examples.db.validation.contacts.MyParameterNameProvider;
import info.ejava.examples.db.validation.contacts.constraints.AdultAge;
import info.ejava.examples.db.validation.contacts.constraints.MinAge;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.inject.Named;
import javax.validation.*;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.*;

@Slf4j
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CustomConstraintTest {
    private static Validator validator;

    @BeforeAll
    static void init() {
        validator=Validation.byDefaultProvider()
                .configure()
                .parameterNameProvider(new MyParameterNameProvider())
                .buildValidatorFactory()
                .getValidator();
    }

    public interface VotingGroup {};
    public interface RetiringGroup {};

    @Builder
    public static class ValidatedClass {
        @MinAge(age = 16, tzOffsetHours = -4)
        private LocalDate dob;

        @MinAge(age=16, message = "method validation found violation")
        public LocalDate getDob() {
            return dob;
        }
        public List<@MinAge(age=16, message="collection member violation") LocalDate> getKeyDates() {
            return Collections.singletonList(dob);
        }

        @MinAge(age=18, groups = {VotingGroup.class})
        @MinAge(age=65, groups = {RetiringGroup.class})
        public LocalDate getConditionalDOB() {
            return dob;
        }

        @AdultAge
        public LocalDate getAdultAge() {
            return dob;
        }

        @MinAge(age=16, message="found java.util.Date age({age}) violation")
        public Date getDobAsDate() {
            return Date.from(dob.atStartOfDay().toInstant(ZoneOffset.UTC));
        }

        public ValidatedClass(@MinAge(age=16, message = "ctor parameter violated") @Named("dob") LocalDate dob) {
            this.dob=dob;
        }
    }

    private LocalDate validTeedDOB = LocalDate.now().minusYears(17);
    private LocalDate validAdultDOB = LocalDate.now().minusYears(20);
    private LocalDate invalidDOB = LocalDate.now();

    @Test
    void valid_valid_object() {
        //given
        ValidatedClass object = ValidatedClass.builder().dob(validAdultDOB).build();
        //when
        Set<ConstraintViolation<ValidatedClass>> violations = validator.validate(object);
        //then
        log.info("\n{}", StringUtils.join(details(violations),"\n"));
        then(violations).hasSize(0);
    }

    @Test
    void report_invalid_field() {
        //given
        ValidatedClass object = ValidatedClass.builder().dob(invalidDOB).build();
        //when
        Set<ConstraintViolation<ValidatedClass>> violations = validator.validate(object);
        log.info("\n{}", StringUtils.join(details(violations),"\n"));
        //then
        then(violations).hasSize(5);
        then(errors(violations)).contains(
                "dob:age below minimum(16) age",
                "dob:method validation found violation",
                "keyDates[0].<list element>:collection member violation",
                "adultAge:must be adult age(18)",
                "dobAsDate:found java.util.Date age(16) violation");
    }

    @Test
    void report_invalid_parameter() throws Exception {
        //given
        Constructor<ValidatedClass> ctor = ValidatedClass.class.getConstructor(new Class<?>[]{LocalDate.class});
        //when
        Set<ConstraintViolation<ValidatedClass>> violations = validator
                .forExecutables()
                .validateConstructorParameters(ctor, new Object[]{ invalidDOB });
        log.info("\n{}", StringUtils.join(details(violations),"\n"));
        //then
        then(violations).hasSize(1);
        then(errors(violations)).contains(
                ValidatedClass.class.getSimpleName() + ".dob:ctor parameter violated"
        );
    }


    static Stream<Arguments> group_dob_violations() {
        LocalDate youthDOB = LocalDate.now().minusYears(16);
        LocalDate youngAdultDOB = LocalDate.now().minusYears(21);
        LocalDate seniorDOB = LocalDate.now().minusYears(66);
        return Stream.of(
                Arguments.of(VotingGroup.class, youthDOB, 1),
                Arguments.of(VotingGroup.class, youngAdultDOB, 0),
                Arguments.of(RetiringGroup.class, youngAdultDOB, 1),
                Arguments.of(RetiringGroup.class, seniorDOB, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("group_dob_violations")
    void repeated_condition_validated(Class<?> group, LocalDate dob, int errorCount) {
        //given
        ValidatedClass object = ValidatedClass.builder().dob(dob).build();
        //when
        Set<ConstraintViolation<ValidatedClass>> violations = validator
                .validateProperty(object, "conditionalDOB", group);
        log.info("\n{}", StringUtils.join(details(violations),"\n"));
        //then
        then(violations).hasSize(errorCount);
    }

    @Builder
    public static class InvalidDeclaration {
        @MinAge(age = -16)
        private LocalDate dob;
    }

    @Test
    void invalid_declaration_throws_exception() {
        //given
        InvalidDeclaration object = InvalidDeclaration.builder().dob(validTeedDOB).build();
        //then
        assertThatThrownBy(
                ()->validator.validate(object))
                .isInstanceOf(ValidationException.class)
                .hasRootCauseMessage("age constraint cannot be negative");
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
