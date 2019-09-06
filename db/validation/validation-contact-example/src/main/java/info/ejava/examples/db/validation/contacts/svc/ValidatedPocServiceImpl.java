package info.ejava.examples.db.validation.contacts.svc;

import info.ejava.examples.db.validation.contacts.dto.PersonPocDTO;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.validation.annotation.Validated;

/**
 * This version of PocService enables validation on the Java method calls.
 */
@Validated
@RequiredArgsConstructor
public class ValidatedPocServiceImpl implements PocService {
    @Delegate(excludes = Overrides.class)
    private final PocService impl;

    /*
     * Not necessary. Just providing example of how to override @Delegate methods
     */
    public PersonPocDTO createPOC(PersonPocDTO poc) { return impl.createPOC(poc); }
    private interface Overrides {
        public PersonPocDTO createPOC(PersonPocDTO poc);
    }
}
