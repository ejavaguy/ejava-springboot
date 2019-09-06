package info.ejava.examples.db.validation.contacts.demo;

import info.ejava.examples.db.validation.contacts.ValidatorConfiguration;
import info.ejava.examples.db.validation.contacts.dto.factories.ContactDTOFactory;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.BDDAssertions.*;

public class GraphTest {
    private ContactDTOFactory contactFactory = new ContactDTOFactory();
    private Validator validator = new ValidatorConfiguration().validator();

    static class Child {
        @NotNull
        String cannotBeNull;
    }
    static class NonTraversingParent {
        Child child = new Child();
    }
    static class TraversingParent {
        @Valid
        Child child = new Child();
    }

    @Test
    void no_validate_does_not_traverse_child() {
        //given
        Object nonTraversing = new NonTraversingParent();
        //when
        Set<ConstraintViolation<Object>> violations = validator.validate(nonTraversing);
        //then
        then(violations).isEmpty();
    }

    @Test
    void validate_traverses_child() {
        //given
        Object traversing = new TraversingParent();
        //when
        Set<ConstraintViolation<Object>> violations = validator.validate(traversing);
        //then
        String errorMsgs = violations.stream()
                .map(v->v.getPropertyPath().toString()+":"+v.getMessage())
                .collect(Collectors.joining("\n"));
        then(errorMsgs).contains("child.cannotBeNull:must not be null");
        then(violations).hasSize(1);
    }
}
