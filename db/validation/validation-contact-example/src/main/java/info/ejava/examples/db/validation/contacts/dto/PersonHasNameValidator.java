package info.ejava.examples.db.validation.contacts.dto;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

/**
 * This validator will return true if either the first or lastName of the person DTO
 * is non-null.
 */
public class PersonHasNameValidator implements ConstraintValidator<PersonHasName, PersonPocDTO> {
    @Override
    public void initialize(PersonHasName constraintAnnotation) {
    }

    @Override
    public boolean isValid(PersonPocDTO person, ConstraintValidatorContext context) {
        if (person==null) {
            return true;
        }
        return (StringUtils.isNotBlank(person.getFirstName()) || StringUtils.isNotBlank(person.getLastName()));
    }
}
