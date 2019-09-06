package info.ejava.examples.db.validation.contacts.svc;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Component
public class ValidatedComponentImpl implements ValidatedComponent {
    @Override
    //Overriding an interface with an additional constraint throws a ConstraintDeclarationException
    //unless overridden by vendor-specific property
    // e.g., hibernate.validator.allow_parameter_constraint_override=true
//    public void aCall(@Positive int mustBeGE5) {
    public void aCall(int mustBeGE5) {
    }
}
