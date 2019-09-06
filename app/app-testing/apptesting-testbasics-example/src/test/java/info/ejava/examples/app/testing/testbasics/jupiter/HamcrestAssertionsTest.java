package info.ejava.examples.app.testing.testbasics.jupiter;

import info.ejava.examples.app.testing.testbasics.PeopleFactory;
import info.ejava.examples.app.testing.testbasics.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.exparity.hamcrest.date.DateMatchers;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class HamcrestAssertionsTest {
    Person beaver = PeopleFactory.beaver();
    Person wally = PeopleFactory.wally();
    Person eddie = PeopleFactory.eddie();

    //core
    @Test
    public void basicTypes() {
        assertThat(beaver.getFirstName(), equalTo("Jerry"));
        assertThat("name", beaver.getFirstName(), equalTo("Jerry"));
    }
    //dates
    @Test
    public void dateTypes() {
        //support for date matchers requires additional org.exparity:hamcrest-date library
        assertThat(beaver.getDob(), DateMatchers.after(wally.getDob()));
        assertThat("beaver NOT younger than wally", beaver.getDob(), DateMatchers.after(wally.getDob()));
    }

    //exceptions
    @Test
    public void exceptions() {
        //no support for exceptions -- Assertions.assertThrows is a JUnit Jupiter call
        RuntimeException ex1 = Assertions.assertThrows(RuntimeException.class,
                () -> {
                    throw new IllegalArgumentException("example exception");
                });
        //this is a simple String match and not specific to exceptions
        assertThat(ex1.getMessage(), equalTo("example exception"));
    }

    //all
    @Test
    public void all() {
        Person p = beaver; //change to eddie to cause failures
        //can only test multiple assertions against same subject
        assertThat(p.getFirstName(), allOf(
                startsWith("J"),
                endsWith("y"),
                equalTo("Jerry")));
    }
}
