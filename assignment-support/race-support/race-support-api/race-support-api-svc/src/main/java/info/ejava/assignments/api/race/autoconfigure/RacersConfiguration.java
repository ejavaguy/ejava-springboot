package info.ejava.assignments.api.race.autoconfigure;

import info.ejava.assignments.api.race.racers.*;
import info.ejava.assignments.api.race.client.factories.RacerDTOFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
public class RacersConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public RacerDTOFactory racerDTOFactory() {
        return new RacerDTOFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public RacersRepository racersRepository() {
        return new RacersRepositoryImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public RacersService racersService(RacersRepository racersRepository) {
        return new RacersServiceImpl(racersRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    public RacersController racersController(RacersService racersService) {
        return new RacersController(racersService);
    }

    @Bean
    @ConditionalOnMissingBean
    public RacersExceptionAdvice racersExceptionAdvice() {
        return new RacersExceptionAdvice();
    }
}
