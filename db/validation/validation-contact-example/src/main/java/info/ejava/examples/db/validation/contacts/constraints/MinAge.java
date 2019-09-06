package info.ejava.examples.db.validation.contacts.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines a minimum age based upon a LocalDate, the current
 * LocalDate, and a specified timezone.
 */

@Documented //include this in Javadoc for elements that it is defined
@Target({
        ElementType.METHOD, //method return values; @MinAge LocalDate getDob();
        FIELD, //declared fields; @MinAge LocalDate dob;
        ANNOTATION_TYPE, //can be composed (defined, reused, extended) by other constraint annotations
        PARAMETER, //method and constructor parameters; ctor(@MinAge LocalDate dob)
        TYPE_USE, //parameterized types; List<@MinAge LocalDate> dobs
        TYPE, //interface or class types; @MinAge class Person
//        CONSTRUCTOR //validate return type of a constructor
})
@Retention(
        //SOURCE - annotation discarded by compiler
        //CLASS - annotation available in class file but not loaded at runtime - default
        RetentionPolicy.RUNTIME //annotation available thru reflection at runtime
)
@Repeatable(value= MinAge.List.class)
@Constraint(validatedBy = {
        MinAgeLocalDateValidator.class,
        MinAgeDateValidator.class
})
public @interface MinAge {
    /**
     * Default error message for when constraint violated. The calculated and minimum
     * age are supplied within the text.
     */
    String message() default "age below minimum({age}) age";

    /**
     * Assigned validation groups for this constraint.
     */
    Class<?>[] groups() default {};

    /**
     * Required array of metadata extending Payload
     */
    Class<? extends Payload>[] payload() default {};

    int age() default 0;
    int tzOffsetHours() default 0;

    /**
     * MinAge constraints for target based on validaton group conditions
     */
    @Documented
    @Retention(RUNTIME)
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, TYPE_USE })
    public @interface List {
        MinAge[] value();
    }
}
