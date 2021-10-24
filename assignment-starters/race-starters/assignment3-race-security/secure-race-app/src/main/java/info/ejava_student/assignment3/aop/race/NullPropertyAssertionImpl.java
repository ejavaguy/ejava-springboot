package info.ejava_student.assignment3.aop.race;

import info.ejava.assignments.aop.race.NullPropertyAssertion;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class NullPropertyAssertionImpl extends NullPropertyAssertion {

    /**
     * Return the named Method for the object or null if
     * method does not exist.
     */
    protected Method hasGetterMethod(Object dto, String getterName) {
        return null; //TODO
    }

    /**
     * Return the value returned from the getter method and report
     * any errors that with a server-type error.
     */
    protected Object getValue(Object object, Method getterMethod) {
        return null; //TODO
    }
}
