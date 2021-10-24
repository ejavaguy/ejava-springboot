package info.ejava.assignments.aop.race;

import info.ejava.examples.common.exceptions.ClientErrorException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

//This class is complete. Student implements subclass implementing abstract methods.
public abstract class NullPropertyAssertion {
    public void assertNull(Object object, String...property) {
        Arrays.stream(property).forEach(p->assertNull(object, p));
    }

    public void assertNull(Object object, String property){
        Method getterMethod=null;
        if (null==object || (getterMethod=hasGetterMethod(object, getterName(property)))==null) {
            return;
        }

        Object nullValue = getValue(object, getterMethod);
        if (null!=nullValue) {
            throw new ClientErrorException.InvalidInputException(
                    "%s: must be null, value=%s", property, nullValue);
        }
    }
    protected String getterName(String property) {
        return "get" + StringUtils.capitalize(property);
    }

    /**
     * This method obtains a Method to the property getter using the class
     * of the provided object and getter method name.
     * @param object
     * @param getterName
     * @return method for getterName in class or null if does not exist
     */
    protected abstract Method hasGetterMethod(Object object, String getterName);

    /**
     * This method will return the value from the method.
     * @param object
     * @param getterMethod
     * @return result of calling method against object
     */
    protected abstract Object getValue(Object object, Method getterMethod);
}
