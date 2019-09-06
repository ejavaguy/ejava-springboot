package info.ejava.examples.svc.aop.items.aspects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
@RequiredArgsConstructor
public class MyInvocationHandler implements InvocationHandler {
    private final Object target;

    public static Object newInstance(Object target) {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                ClassUtils.getAllInterfaces(target.getClass()).toArray(new Class[0]),
                new MyInvocationHandler(target));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //do work ...
        log.info("invoke calling: {}({})", method.getName(), args);

        Object result = method.invoke(target, args);

        //do work ...
        log.info("invoke {} returned: {}", method.getName(), result);

        //return result
        return result;
    }
}
