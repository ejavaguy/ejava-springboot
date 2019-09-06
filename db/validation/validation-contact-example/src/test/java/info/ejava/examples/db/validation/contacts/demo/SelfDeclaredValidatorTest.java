package info.ejava.examples.db.validation.contacts.demo;

import info.ejava.examples.db.validation.contacts.MyParameterNameProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.validation.annotation.Validated;

import javax.inject.Named;
import javax.validation.*;
import javax.validation.constraints.NotNull;
import javax.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.util.Set;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * This test demonstrates that the
 */
@Slf4j
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class SelfDeclaredValidatorTest {
    private static Validator validator;
    private static ExecutableValidator executableValidator;

    @BeforeAll
    static void init() {
        //ValidatorFactory myValidatorFactory = Validation.buildDefaultValidatorFactory();
        ValidatorFactory myValidatorFactory = Validation.byDefaultProvider()
                .configure()
                .parameterNameProvider(new MyParameterNameProvider())
                .buildValidatorFactory();
        validator = myValidatorFactory.getValidator();
        executableValidator = validator.forExecutables();
    }

    @Getter
    private static class AClass {
        @NotNull
        private String aValue;
    };

    private static class AService {
        @NotNull
//        public String aMethod(@NotNull @Valid AClass aParameter) {
        public String aMethod(@NotNull @Valid @Named("aParameter") AClass aParameter) {
            return aParameter.toString();
        }
    }

    @Test
    void self_declared_validator() {
        //given - a validator instantiated on the fly
        ValidatorFactory myValidatorFactory = Validation.buildDefaultValidatorFactory();
        Validator myValidator = myValidatorFactory.getValidator();
        AClass invalidAClass = new AClass();

        //when
        Set<ConstraintViolation<AClass>> violations = myValidator.validate(invalidAClass);
        for (ConstraintViolation v: violations) {
            log.info("field name={}, value={}, violated={}, d={}", v.getPropertyPath(), v.getInvalidValue(), v.getMessage(), v.getConstraintDescriptor());
        }
        //then
        then(violations).isNotEmpty();
    }

    @Test
    void initialized_validator() {
        //given
        AClass invalidAClass = new AClass();

        //when
        Set<ConstraintViolation<AClass>> violations = validator.validate(invalidAClass);
        for (ConstraintViolation v: violations) {
            log.info("field name={}, value={}, violated={}", v.getPropertyPath(), v.getInvalidValue(), v.getMessage());
        }
        //then
        then(violations).isNotEmpty();
    }

    @Test
    void method_arguments() throws NoSuchMethodException {
        //given
        AService myService = new AService();
        AClass myValue = new AClass();
        Object[] methodParams = new Object[]{ myValue };
        Class<?>[] methodParamTypes = new Class<?>[]{ AClass.class };
        Method methodToCall = AService.class.getMethod("aMethod", methodParamTypes);
        //when
        Set<ConstraintViolation<AService>> violations = validator
                .forExecutables().validateParameters(myService, methodToCall, methodParams);
        //then
        then(violations).hasSize(1);
        ConstraintViolation<?> violation = violations.iterator().next();
//        then(violation.getPropertyPath().toString()).isEqualTo("aMethod.arg0.aValue");
        then(violation.getPropertyPath().toString()).isEqualTo("aMethod.aParameter.aValue");
        then(violation.getMessage()).isEqualTo("must not be null");
        then(violation.getInvalidValue()).isEqualTo(myValue.getAValue());

        //given
        String nullResult = null;
        //when
        violations = validator.forExecutables()
                .validateReturnValue(myService, methodToCall, nullResult);
        //then
        then(violations).hasSize(1);
        violation = violations.iterator().next();
        then(violation.getPropertyPath().toString()).isEqualTo("aMethod.<return value>");
        then(violation.getMessage()).isEqualTo("must not be null");
        then(violation.getInvalidValue()).isEqualTo(nullResult);
    }
}
