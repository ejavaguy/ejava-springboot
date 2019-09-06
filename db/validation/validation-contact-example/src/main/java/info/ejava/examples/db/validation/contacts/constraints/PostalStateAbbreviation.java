package info.ejava.examples.db.validation.contacts.constraints;


import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This constraint will enforce the value to be equal to one of the
 * recognized US postal state abbreviations.
 */
@Documented
@Target({METHOD, FIELD, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(value= PostalStateAbbreviation.List.class)
@Constraint(validatedBy = {PostalStateAbbreviationValidator.class})
public @interface PostalStateAbbreviation {
    String message() default "not a recognized postal state abbreviation";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Set to false if the value must match case.
     */
    boolean normalize() default true;

    @Documented
    @Retention(RUNTIME)
    @Target({METHOD, FIELD, PARAMETER, TYPE_USE})
    public @interface List {
        PostalStateAbbreviation[] value();
    }
}
