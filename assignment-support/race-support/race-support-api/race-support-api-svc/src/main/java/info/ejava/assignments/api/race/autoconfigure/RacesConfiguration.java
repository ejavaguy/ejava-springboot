package info.ejava.assignments.api.race.autoconfigure;

import info.ejava.assignments.api.race.races.*;
import info.ejava.assignments.api.race.client.factories.RaceDTOFactory;
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
    @ConditionalOnMissingBean
    public RacesController racesController(RacesService racesService) {
        return new RacesController(racesService);
    }

    @Bean
    @ConditionalOnMissingBean
    public RacesExceptionAdvice exceptionAdvice() {
        return new RacesExceptionAdvice();
    }
}
