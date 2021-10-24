package info.ejava_student.assignment3.aop.race.config;


import info.ejava.assignments.aop.race.NullPropertyAssertion;
import info.ejava_student.assignment3.aop.race.NullPropertyAssertionImpl;
import info.ejava_student.assignment3.aop.race.ValidatorAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AOPConfiguration {
    @Bean
    public NullPropertyAssertion nullPropertyAssertion() {
        return new NullPropertyAssertionImpl();
    }

    @Bean
    @Profile("aop")
    public ValidatorAspect validatorAspect(NullPropertyAssertion nullPropertyAssertion) {
        return new ValidatorAspect(nullPropertyAssertion);
    }
}
