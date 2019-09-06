package info.ejava.examples.db.validation.contacts.dto;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A person is required to have either a first or last name.
 * One or the other can be null but not both.
 */
@Documented
@Constraint(validatedBy = PersonHasNameValidator.class )
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PersonHasName {
    String message() default "first and/or lastName must be supplied";
    Class<? extends Payload>[] payload() default {};
    Class<?>[] groups() default {};

    @Documented
    @Retention(RUNTIME)
    @Target({METHOD, FIELD, PARAMETER, TYPE_USE})
    public @interface List {
        PersonHasName[] value();
    }
}

