package info.ejava.assignments.api.race.racers;

import info.ejava.examples.common.web.BaseExceptionAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = RacersController.class)
public class RacersExceptionAdvice extends BaseExceptionAdvice {
}
