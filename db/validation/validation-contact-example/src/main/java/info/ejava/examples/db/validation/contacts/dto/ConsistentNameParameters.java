package info.ejava.examples.db.validation.contacts.dto;

import org.apache.commons.lang3.StringUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import java.lang.annotation.*;

/**
 * This is an example of a cross-parameter constraint. Where
 * the state of multiple parameters in context with one another is used
 * to determine if valid.
 */
@Documented
@Constraint(validatedBy = ConsistentNameParameters.ConsistentNameParametersValidator.class )
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConsistentNameParameters {
    String message() default "first and/or second must be supplied";
    Class<? extends Payload>[] payload() default {};
    Class<?>[] groups() default {};

    /**
     * This validator verifies the condition where either firstName or lastName is supplied
     * as the members of a method call.
     */
    @SupportedValidationTarget(ValidationTarget.PARAMETERS)
    public class ConsistentNameParametersValidator implements ConstraintValidator<ConsistentNameParameters, Object[]> {

        /**
         * name1 and name2 are in argument positions 1 and 2.
         * @param values
         * @param context
         * @return true if valid
         */
        @Override
        public boolean isValid(Object[] values, ConstraintValidatorContext context) {
            if (values.length != 4) {
                throw new IllegalArgumentException(
                        String.format("Unexpected method signature, 4 params expected, %d supplied", values.length));
            }
            for (int i=1; i<3; i++) { //look at positions 1 and 2
                if (values[i]!=null && !(values[i] instanceof String)) {
                    throw new IllegalArgumentException(
                            String.format("Illegal method signature, param[%d], String expected, %s supplied",
                                    i, values[i].getClass()));
                }
            }

            String name1= (String) values[1];
            String name2= (String) values[2];
            if (!(StringUtils.isNotBlank(name1) || StringUtils.isNotBlank(name2))) {
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addParameterNode(1)
                        .addConstraintViolation()
                        .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addParameterNode(2)
                        .addConstraintViolation();
                //the following removes default-generated message
                //context.disableDefaultConstraintViolation();
                return false;
            }
            //could have just done this if used default response
            return (StringUtils.isNotBlank(name1) || StringUtils.isNotBlank(name2));
        }
    }
}
