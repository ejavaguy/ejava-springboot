package info.ejava.assignments.race.config;

import info.ejava.assignments.race.autoconfigure.RacersConfiguration;
import info.ejava.assignments.race.autoconfigure.RacesConfiguration;
import info.ejava.examples.common.web.paging.RestTemplateConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {RacesConfiguration.class, RacersConfiguration.class})
public class RaceTestConfiguration {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return new RestTemplateConfig().restTemplateDebug(builder);
    }
}
