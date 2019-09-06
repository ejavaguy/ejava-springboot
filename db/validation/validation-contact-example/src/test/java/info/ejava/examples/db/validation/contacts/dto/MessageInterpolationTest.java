package info.ejava.examples.db.validation.contacts.dto;

import info.ejava.examples.db.validation.contacts.dto.factories.ContactDTOFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.BDDAssertions.then;

@Slf4j
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MessageInterpolationTest {
    private static ContactDTOFactory contactDTOFactory = new ContactDTOFactory();
    private static Validator validator;

    @BeforeAll
    static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void small_phone_number() {
        //given
        ContactPointDTO contact = contactDTOFactory.make();
        contact.setPhone("123"); //numer is too small
        //when
        Set<ConstraintViolation<ContactPointDTO>> violations = validator.validate(contact);
        //then
        then(violations).hasSize(2);
        List<String> messages = violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
        log.info("\n"+StringUtils.join(messages,"\n"));
        then(messages).contains(
                "123 must have 8 characters",
                ">>       123<< must contain (3 digit)-(4 digit) number");
    }
}
