package info.ejava.assignments.race.autoconfigure;

import info.ejava.assignments.race.client.factories.RaceDTOFactory;
import info.ejava.assignments.race.races.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
public class RacesConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public RaceDTOFactory raceDTOFactory() {
        return new RaceDTOFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public RacesRepository racesRepository() {
        return new RacesRespositoryImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public RacesService racesService(RacesRepository racesRepository) {
        return new RacesServiceImpl(racesRepository);
    }

    @Bean
    public RacesController racesController(RacesService racesService) {
        return new RacesController(racesService);
    }

    @Bean
    public RacesExceptionAdvice exceptionAdvice() {
        return new RacesExceptionAdvice();
    }
}
