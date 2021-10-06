package info.ejava.examples.svc.content.quotes;

import info.ejava.examples.common.web.RestTemplateLoggingFilter;
import info.ejava.examples.common.webflux.WebClientLoggingFilter;
import info.ejava.examples.svc.content.quotes.client.QuotesAPIClient;
import info.ejava.examples.svc.content.quotes.client.ServerConfig;
import info.ejava.examples.svc.content.quotes.dto.QuoteDTOFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

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
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.requestFactory(
                        //used to read the streams twice -- so we can use the logging filter below
                        ()->new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .interceptors(List.of(new RestTemplateLoggingFilter()))
                .build();
        return restTemplate;
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .filter(WebClientLoggingFilter.requestFilter())
                .filter(WebClientLoggingFilter.responseFilter())
                .build();
    }

    @Bean
    @Qualifier("webclient")
    public QuotesAPIClient quotesWebClient(WebClient webClient, ServerConfig cfg) {
        return new QuotesAPIClient(webClient, cfg);
    }

    @Bean
    public QuoteDTOFactory quotesDtoFactory() {
        return new QuoteDTOFactory();
    }
}
