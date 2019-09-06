package info.ejava.examples.svc.aop.items.aspects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@Slf4j
public class MyMethodInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        //do work ...
        log.info("invoke calling: {}({})", method.getName(), args);

        Object result = methodProxy.invokeSuper(proxy, args);

        //do work ...
        log.info("invoke {} returned: {}", method.getName(), result);

        //return result
        return result;
    }
}
