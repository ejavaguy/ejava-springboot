package info.ejava.examples.db.validation.contacts.svc;

import org.springframework.validation.annotation.Validated;

import javax.inject.Named;
import javax.validation.constraints.Min;

@Validated
public interface ValidatedComponent {
    void aCall(@Min(5) @Named("mustBeGE5") int mustBeGE5);
}
