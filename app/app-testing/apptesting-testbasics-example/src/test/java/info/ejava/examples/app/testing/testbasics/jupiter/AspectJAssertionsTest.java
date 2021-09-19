package info.ejava.examples.app.testing.testbasics.jupiter;

import info.ejava.examples.app.testing.testbasics.Assertions;
import info.ejava.examples.app.testing.testbasics.PeopleFactory;
import info.ejava.examples.app.testing.testbasics.Person;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.BDDAssertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static info.ejava.examples.app.testing.testbasics.BddAssertions.*;
import static org.assertj.core.api.BDDAssertions.thenExceptionOfType;

@Slf4j
public class AspectJAssertionsTest {
    Person beaver = PeopleFactory.beaver();
    Person wally = PeopleFactory.wally();
    Person eddie = PeopleFactory.eddie();

    //core
    @Test
    public void basicTypes() {
        assertThat(beaver.getFirstName()).isEqualTo("Jerry");
        assertThat(beaver.getFirstName()).as("name").isEqualTo("Jerry");
    }
    //dates
    @Test
    public void dateTypes() {
        assertThat(beaver.getDob()).isAfter(wally.getDob());
        assertThat(beaver.getDob()).as("beaver NOT younger than wally").isAfter(wally.getDob());
    }

    //exceptions
    @Test
    public void exceptions() {
        assertThatThrownBy(
                () -> {
                    throw new IllegalArgumentException("example exception");
                }).hasMessage("example exception");

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(
                () -> {
                    throw new IllegalArgumentException("example exception");
                }).withMessage("example exception");


        Throwable ex1 = catchThrowable(
                ()->{ throw new IllegalArgumentException("example exception"); });
        assertThat(ex1).hasMessage("example exception");

        RuntimeException ex2 = catchThrowableOfType(
                ()->{
                    throw new IllegalArgumentException("example exception");
                    },
                RuntimeException.class);
        assertThat(ex2).hasMessage("example exception");
    }

    //BDD exceptions
    @Test
    public void bdd_exceptions() {
        //BDD syntax only has evaluations
        BDDAssertions.thenThrownBy(()-> {
                    throw new IllegalArgumentException("example exception");
                }).hasMessage("example exception");

        thenExceptionOfType(RuntimeException.class).isThrownBy(
                () -> {
                    throw new IllegalArgumentException("example exception");
                }).withMessage("example exception");
    }

    //all
    @Test
    public void all() {
        Person p = beaver; //change to eddie to cause failures
        //Person p = eddie;
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(p.getFirstName()).isEqualTo("Jerry");
        softly.assertThat(p.getLastName()).isEqualTo("Mathers");
        softly.assertThat(p.getDob()).isAfter(wally.getDob());

        log.info("error count={}", softly.errorsCollected().size());
        softly.assertAll();
    }

    @Test
    public void extensions() {
        Assertions.assertThat(beaver)
                .hasFirstName("Jerry")
                .hasLastName("Mathers");
        then(beaver)
                .hasFirstName("Jerry")
                .hasLastName("Mathers");
        and.then(beaver)
                .hasFirstName("Jerry")
                .hasLastName("Mathers");
    }
}
