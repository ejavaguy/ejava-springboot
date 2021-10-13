package info.ejava_student.assignment3.security.race.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
@EnableConfigurationProperties
@EnableAutoConfiguration
@Slf4j
public class RaceTestConfiguration {
    @Value("${spring.security.user.name:}")
    private String username;
    @Value("${spring.security.user.password:}")
    private String password;

    @Bean
    public RestTemplate anonymousUser(RestTemplateBuilder builder) {
        return builder.build(); //TODO??
    }
    @Bean
    @ConditionalOnProperty(prefix = "spring.security.user", name={"name","password"})
    public RestTemplate authnUser(RestTemplateBuilder builder) {
        return builder.build(); //TODO
    }
    @Bean
    public RestTemplate badUser(RestTemplateBuilder builder) {
        return builder.build(); //TODO
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.security.user", name={"name"})
    public String authnUsername() {
        return username;
    }
    @Bean
    public String anonymousUsername() {
        return "(null)";
    }
}
