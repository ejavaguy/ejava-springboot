package info.ejava_student.assignment3.security.race.racerregistrations.config;


import info.ejava.assignments.api.race.racers.RacersService;
import info.ejava.assignments.api.race.races.RacesService;
import info.ejava.assignments.security.race.security.AuthorizationHelper;
import info.ejava_student.assignment3.security.race.racerregistrations.SecureRacerRegistrationServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
//@AutoConfigureBefore(RacerRegistrationConfiguration.class)
public class RacerRegistrationSecurityConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public Object secureRacerRegistrationService(
            Object racerRegRepo,
            RacesService racesService,
            RacersService racersService,
            AuthorizationHelper authzHelper) {
        //...
        return new SecureRacerRegistrationServiceImpl(/* ... */);
    }
}
