package info.ejava.examples.db.validation.contacts.demo;

import info.ejava.examples.db.validation.contacts.NTestConfiguration;
import info.ejava.examples.db.validation.contacts.ValidatorConfiguration;
import info.ejava.examples.db.validation.contacts.dto.ContactPointDTO;
import info.ejava.examples.db.validation.contacts.dto.PersonPocDTO;
import info.ejava.examples.db.validation.contacts.svc.PocService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.BDDAssertions.then;

@ExtendWith(MockitoExtension.class)
@Slf4j
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ExecutionValidatorTest {
    private ExecutableValidator execValidator;

    @BeforeEach
    void init() {
        Validator validator = new ValidatorConfiguration().validator();
        execValidator = validator.forExecutables();
    }

    private List<String> errors(Set<?> violations) {
        return violations.stream()
                .map(v->((ConstraintViolation)v).getPropertyPath().toString() +
                        ":" +
                        ((ConstraintViolation)v).getMessage())
                .collect(Collectors.toList());
    }

    @Nested
    public class ctor_validation_identifies {
        //given
        private Constructor<ContactPointDTO> ctor;
        private Class[] ctorTypes =new Class[]{String.class, String.class, String.class, String.class};
        private Object[] ctorArgs = new Object[]{ null, null, null, null};

        @BeforeEach
        void init() throws Exception {
            ctor = ContactPointDTO.class.getConstructor(ctorTypes);
        }

        @Test
        public void invalid_input_arguments() throws Exception {
            //when
            Set<ConstraintViolation<ContactPointDTO>> violations =
                    execValidator.validateConstructorParameters(ctor, ctorArgs);
            //then
            then(violations).hasSize(1);
            log.info("\n{}", StringUtils.join(errors(violations)+"\n"));
            then(errors(violations)).contains(
                    "ContactPointDTO.id:must not be null"
            );
        }

        @Test
        public void invalid_result() throws Exception {
            //given
            Object object = ctor.newInstance(ctorArgs);
            //when
            Set<ConstraintViolation<Object>> violations =
                    execValidator.validateConstructorReturnValue(ctor, object);
            //then
            then(violations).hasSize(1);
            log.info("\n{}", StringUtils.join(errors(violations)+"\n"));
            then(errors(violations)).contains(
                    "ContactPointDTO.<return value>.name:must not be null"
            );
        }
    }

    @Nested
    public class method_validation_identifies {
        //given
        @Mock
        private PocService pocServiceMock;
        private Method method;
        private Class<?>[] argTypes = new Class<?>[]{ String.class};
        private Object[] argValues = new Object[]{ null };

        @BeforeEach
        void init() throws Exception {
            method = PocService.class.getMethod("getPOC", String.class);
        }

        @Test
        public void method_input_validator() throws NoSuchMethodException {
            //when
            Set<ConstraintViolation<PocService>> violations =
                    execValidator.validateParameters(pocServiceMock, method, argValues);
            //then
            then(violations).hasSize(1);
            ConstraintViolation violation = violations.iterator().next();
            then(violation.getPropertyPath().toString()).isEqualTo(method.getName() + ".id");
            then(violation.getMessage()).isEqualTo("must not be null");
            then(violation.getInvalidValue()).isEqualTo(null);
        }

        @Test
        public void method_return_validator() throws Exception {
            //given
            String validId = "123";
            BDDMockito.given(pocServiceMock.getPOC(validId)).willReturn(null);
            Optional<PersonPocDTO> poc = (Optional<PersonPocDTO>) method.invoke(pocServiceMock, validId);
            //when
            Set<ConstraintViolation<PocService>> violations =
                    execValidator.validateReturnValue(pocServiceMock, method, poc);
            //then
            then(violations).hasSize(1);
            ConstraintViolation<PocService> violation = violations.iterator().next();
            then(violation.getPropertyPath().toString()).isEqualTo(method.getName() + ".<return value>");
            then(violation.getMessage()).isEqualTo("must not be null");
            then(violation.getInvalidValue()).isNull();
        }
    }
}
