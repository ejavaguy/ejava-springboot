package info.ejava.examples.db.validation.contacts.demo;

import info.ejava.examples.db.validation.contacts.MyParameterNameProvider;
import info.ejava.examples.db.validation.contacts.dto.ConsistentNameParameters;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.BDDAssertions.*;

@Slf4j
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CrossParameterTest {
    private static Validator validator;

    static class NamedThing {
        private String id;
        private String name1;
        private String name2;
        private LocalDate dob;

        @ConsistentNameParameters(message = "name1 and/or name2 must be supplied")
        public NamedThing(String id, @Named("name1") String name1, @Named("name2") String name2, LocalDate dob) {
            this.id=id;
            this.name1 = name1;
            this.name2 = name2;
            this.dob = dob;
        }
    }
    static Constructor<NamedThing> ctor;

    @BeforeAll
    static void init() throws NoSuchMethodException {
        validator= Validation.byDefaultProvider()
                .configure()
                .parameterNameProvider(new MyParameterNameProvider())
                .buildValidatorFactory()
                .getValidator();
        Class<?>[] ctorTypes = new Class<?>[]{String.class, String.class, String.class, LocalDate.class};
        ctor = NamedThing.class.getConstructor(ctorTypes);
    }


    static Stream<Arguments> consistentNames() {
        return Stream.of(
                Arguments.of("name1", "name2"),
                Arguments.of("name1", null),
                Arguments.of(null, "name2")
        );
    }

    @ParameterizedTest
    @MethodSource("consistentNames")
    void accepts_consistent_names_params(String name1, String name2) throws NoSuchMethodException {
        //given
        Object[] ctorParams = new Object[]{ "123", name1, name2, LocalDate.now()};
        //when
        Set<ConstraintViolation<NamedThing>> violations =
                validator.forExecutables().validateConstructorParameters(ctor, ctorParams);
        log.info("{}", StringUtils.join(details(violations),"\n"));
        //then
        then(violations).hasSize(0);
    }

    @Test
    void rejects_inconsistent_names_params() {
        //given
        String nullName = null;
        Object[] ctorParams = new Object[]{ "123", nullName, nullName, LocalDate.now()};
        //when
        Set<ConstraintViolation<NamedThing>> violations =
                validator.forExecutables().validateConstructorParameters(ctor, ctorParams);
        log.info("\n{}", StringUtils.join(details(violations),"\n"));
        //then
        then(errors(violations)).contains(
                "NamedThing.<cross-parameter>:name1 and/or name2 must be supplied",
                "NamedThing.name1:name1 and/or name2 must be supplied",
                "NamedThing.name2:name1 and/or name2 must be supplied"
        );
        then(violations).hasSize(3);
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
                        "=" + Arrays.toString((Object[])v.getInvalidValue()) +
                        ":" +
                        v.getMessage())
                .collect(Collectors.toList());
    }
}
