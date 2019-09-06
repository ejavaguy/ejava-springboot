package info.ejava.examples.svc.httpapi.gestures;

import info.ejava.examples.common.web.RestTemplateLoggingFilter;
import info.ejava.examples.common.webflux.WebClientLoggingFilter;
import info.ejava.examples.svc.httpapi.gestures.api.GesturesAPI;
import info.ejava.examples.svc.httpapi.gestures.client.GesturesAPITemplateClient;
import info.ejava.examples.svc.httpapi.gestures.client.GesturesAPISyncWebClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
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
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.requestFactory(
                //used to read the streams twice
                ()->new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .build();

        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(new RestTemplateLoggingFilter());
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }


    @Bean
    @ConfigurationProperties("it.server")
    public ServerConfig itServerConfig() {
        return new ServerConfig();
    }

    @Bean
    @Primary
    public GesturesAPI gesturesTemplateClient(RestTemplate restTemplate, ServerConfig cfg) {
        return new GesturesAPITemplateClient(restTemplate, cfg);
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
    public GesturesAPI gesturesWebClient(WebClient webClient, ServerConfig cfg) {
        return new GesturesAPISyncWebClient(webClient, cfg);
    }
}
