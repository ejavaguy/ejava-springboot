package info.ejava.assignments.testing.race.racers;

import info.ejava.assignments.testing.race.racers.svc.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "racer.registration", name = "active", havingValue = "true", matchIfMissing = true)
public class RacerRegistrationConfig {
    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("racer.registration")
    public RacerRegistrationProperties racerProperties() {
        return new RacerRegistrationProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public RacerValidator racerValidator() {
        return new RacerValidatorImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public RacerRegistrationService racerRegistrationService(RacerRegistrationProperties props, RacerValidator validator) {
        return new RacerRegistrationServiceImpl(props, validator);
    }
}
