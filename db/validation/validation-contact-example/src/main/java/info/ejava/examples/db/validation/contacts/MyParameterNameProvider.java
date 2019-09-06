package info.ejava.examples.db.validation.contacts;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Named;
import javax.validation.ParameterNameProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MyParameterNameProvider implements ParameterNameProvider {
    @Override
    public List<String> getParameterNames(Constructor<?> ctor) {
        return getParameterNames((Executable) ctor);
    }

    @Override
    public List<String> getParameterNames(Method method) {
        return getParameterNames((Executable) method);
    }

    protected List<String> getParameterNames(Executable method) {
        List<String> argNames = new ArrayList<>(method.getParameterCount());
        for (Parameter p : method.getParameters()) {
            String argName = getNamedName(p);
            if (StringUtils.isBlank(argName)) {
                argName = getPathVariableName(p);
                if (StringUtils.isBlank(argName)) {
                    argName = getRequestParamName(p);
                    if (StringUtils.isBlank(argName)) {
                        argName = p.getName();
                    }
                }
            }

            argNames.add(argName);
        }
        return argNames;
    }

    String getNamedName(Parameter p) {
        Named annotation = p.getAnnotation(Named.class);
        return annotation == null ? null : annotation.value();
    }
    String getPathVariableName(Parameter p) {
        PathVariable annotation = p.getAnnotation(PathVariable.class);
        return annotation == null ? null : annotation.name();
    }
    String getRequestParamName(Parameter p) {
        RequestParam annotation = p.getAnnotation(RequestParam.class);
        return annotation == null ? null : annotation.value();
    }
}
