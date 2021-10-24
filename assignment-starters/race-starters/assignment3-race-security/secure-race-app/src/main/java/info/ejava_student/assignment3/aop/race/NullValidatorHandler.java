package info.ejava_student.assignment3.aop.race;

import info.ejava.assignments.aop.race.NullPropertyAssertion;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

@RequiredArgsConstructor
public class NullValidatorHandler implements InvocationHandler {
    private final Object target;
    private final List<String> methodNames;
    private final NullPropertyAssertion nullPropertyAssertion;
    private final List<String> propertyNames;

    /**
     * Implement the handler method invoked by the dynamic proxy interpose.
     * @param proxy
     * @param method that was invoked by caller
     * @param args to the method invoked, supplied by caller
     * @return value returned from target.method() call if args valid
     * @throws info.ejava.examples.common.exceptions.ClientErrorException.InvalidInputException
     * if args not valid
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null; //TODO
    }

    /**
     * Creates a new dynamic interface proxy for target that will perform
     * nullProperty assertion logic against provided objects for named
     * methods.
     * @param target the object we are proxying
     * @param methodNames methods for the handler to process
     * @param nullPropertyAssertion validatpr for handler to validate with
     * @param propertyNames properties to validate
     * @param <T> target object type
     * @return dynamic proxy implementing same interfaces as target
     */
    public static <T> T newInstance(T target, List<String> methodNames,
                                     NullPropertyAssertion nullPropertyAssertion,
                                     List<String> propertyNames) {
        //HINTS
        ClassUtils.getAllInterfaces(target.getClass()).toArray(new Class[0]);
        new NullValidatorHandler(target, methodNames, nullPropertyAssertion, propertyNames);
        //TODO
        return (T) null;
    }
}
