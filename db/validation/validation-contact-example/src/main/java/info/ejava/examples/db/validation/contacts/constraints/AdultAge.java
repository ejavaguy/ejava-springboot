package info.ejava.examples.db.validation.contacts.constraints;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines a minimum age based upon a LocalDate, the current
 * LocalDate, and a specified timezone.
 */

@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value= AdultAge.List.class)
@Constraint(validatedBy = {})
@MinAge(age=18)
public @interface AdultAge {
    @OverridesAttribute(constraint = MinAge.class) //override with , name="message"
    String message() default "must be adult age({age})";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    /**
     * MinAge constraints for target based on validaton group conditions
     */
    @Documented
    @Retention(RUNTIME)
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE })
    public @interface List {
        AdultAge[] value();
    }
}
