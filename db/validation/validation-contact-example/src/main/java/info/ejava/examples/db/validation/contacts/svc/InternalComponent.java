package info.ejava.examples.db.validation.contacts.svc;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.inject.Named;
import javax.validation.constraints.NegativeOrZero;

@Component
@Validated
public class InternalComponent {
    public void negativeOrZero(@NegativeOrZero(payload = InternalError.class) @Named("value") int value) {
    }
}
