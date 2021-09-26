package info.ejava.assignments.security.race.config;

import info.ejava.assignments.api.race.autoconfigure.RacesConfiguration;
import info.ejava.assignments.api.race.races.RacesRepository;
import info.ejava.assignments.api.race.races.RacesService;
import info.ejava.assignments.api.race.races.RacesServiceImpl;
import info.ejava.assignments.security.race.security.AuthorizationHelper;
import info.ejava.assignments.security.race.races.SecureRacesServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(RacesConfiguration.class)
public class RacesSecurityConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public AuthorizationHelper authzHelper() {
        return new AuthorizationHelper();
    }
    @Bean
    @ConditionalOnMissingBean
    public RacesService secureRacesService(RacesRepository racesRepository, AuthorizationHelper authzHelper) {
        RacesService impl = new RacesServiceImpl(racesRepository);
        return new SecureRacesServiceImpl(impl, authzHelper);
    }
}
