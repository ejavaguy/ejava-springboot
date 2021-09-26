package info.ejava.assignments.security.race.config;

import info.ejava.assignments.api.race.autoconfigure.RacersConfiguration;
import info.ejava.assignments.api.race.racers.RacersRepository;
import info.ejava.assignments.api.race.racers.RacersService;
import info.ejava.assignments.api.race.racers.RacersServiceImpl;
import info.ejava.assignments.security.race.racers.SecureRacersServiceImpl;
import info.ejava.assignments.security.race.security.AuthorizationHelper;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(RacersConfiguration.class)
public class RacersSecurityConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public AuthorizationHelper authzHelper() {
        return new AuthorizationHelper();
    }
    @Bean
    @ConditionalOnMissingBean
    public RacersService secureRacersService(RacersRepository racersRepository, AuthorizationHelper authzHelper) {
        RacersService impl = new RacersServiceImpl(racersRepository);
        return new SecureRacersServiceImpl(impl, authzHelper);
    }
}
