package info.ejava.examples.app.testing.testbasics.jupiter;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.BDDAssertions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class AssertionsTest {
    int lhs=1;
    int rhs=1;
    int expected=2;

    @Test
    void one_and_one() {
        //junit 4/Vintage assertion
        Assert.assertEquals(expected, lhs+rhs);
        //Jupiter assertion
        Assertions.assertEquals(expected, lhs+rhs);
        //hamcrest assertion
        MatcherAssert.assertThat(lhs+rhs, Matchers.is(expected));
        //AssertJ assertion
        org.assertj.core.api.Assertions.assertThat(lhs+rhs).isEqualTo(expected);
        //AssertJ BDD
        BDDAssertions.then(lhs+rhs).isEqualTo(expected);
    }

    @Test
    void one_and_one_description() {
        //junit 4/Vintage assertion
        Assert.assertEquals("math error", expected, lhs+rhs);
        //Jupiter assertions
        Assertions.assertEquals(expected, lhs+rhs, "math error");
        Assertions.assertEquals(expected, lhs+rhs, ()->String.format("math error %d+%d!=%d",lhs,rhs,expected));
        //hamcrest assertion
        MatcherAssert.assertThat("math error",lhs+rhs, Matchers.is(expected));
        //AssertJ assertion
        org.assertj.core.api.Assertions.assertThat(lhs+rhs)
                ;
        org.assertj.core.api.Assertions.assertThat(lhs+rhs)
                .as("math error %d+%d!=%d",lhs,rhs,expected)
                .isEqualTo(expected);
        //AssertJ BDD
        BDDAssertions.then(lhs+rhs)
                .as("math error")
                .isEqualTo(expected);
    }

    @Test
    void junit_all() {
        Assertions.assertAll("all assertions",
        //Jupiter assertions
            () -> Assertions.assertEquals(expected, lhs+rhs, "jupiter assertion"),
            () -> Assertions.assertEquals(expected, lhs+rhs, ()->String.format("jupiter format %d+%d!=%d",lhs,rhs,expected))
        );
    }
}
