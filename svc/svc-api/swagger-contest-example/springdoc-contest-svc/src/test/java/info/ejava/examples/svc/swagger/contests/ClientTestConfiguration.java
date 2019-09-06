package info.ejava.examples.svc.swagger.contests;

import info.ejava.examples.common.web.ServerConfig;
import info.ejava.examples.common.webflux.WebClientLoggingFilter;
import info.ejava.examples.svc.springdoc.contests.dto.ContestDTOFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * A test configuration used by remote IT test clients.
 */
@SpringBootConfiguration()
@EnableConfigurationProperties //used to set it.server properties
@EnableAutoConfiguration       //needed to setup logging
public class ClientTestConfiguration {
    @Bean
    @ConfigurationProperties("it.server")
    public ServerConfig itServerConfig() {
        return new ServerConfig();
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .filter(WebClientLoggingFilter.requestFilter())
                .filter(WebClientLoggingFilter.responseFilter())
                .build();
    }

    @Bean
    public ContestDTOFactory quotesDtoFactory() {
        return new ContestDTOFactory();
    }
}
