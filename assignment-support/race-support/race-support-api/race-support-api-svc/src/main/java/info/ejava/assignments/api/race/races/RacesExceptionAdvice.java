package info.ejava.assignments.api.race.races;

import info.ejava.examples.common.web.BaseExceptionAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = RacesController.class)
public class RacesExceptionAdvice extends BaseExceptionAdvice {
}
