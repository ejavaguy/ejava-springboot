package info.ejava.examples.db.validation.contacts;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@Configuration
public class ValidatorConfiguration {
    @Bean
    public Validator validator() {
            //generic API
        return Validation.byDefaultProvider()
                .configure()
                    //provider-specific property
//                .addProperty("hibernate.validator.allow_parameter_constraint_override", Boolean.TRUE.toString())
                .parameterNameProvider(new MyParameterNameProvider())
                .buildValidatorFactory()
                .getValidator();

            //hibernate-specific configuration API
//        return Validation.byProvider(HibernateValidator.class) //provider-specific
//                .configure()
//                .allowOverridingMethodAlterParameterConstraint(true) //provider-specific
//                .parameterNameProvider(new MyParameterNameProvider())
//                .buildValidatorFactory()
//                .getValidator();
    }
}
