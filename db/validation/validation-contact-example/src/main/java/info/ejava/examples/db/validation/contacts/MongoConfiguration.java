package info.ejava.examples.db.validation.contacts;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;

import javax.validation.Validator;

@Configuration
public class MongoConfiguration {
    @Bean
    public ValidatingMongoEventListener mongoValidator(Validator validator) {
        return new ValidatingMongoEventListener(validator);
    }
}
