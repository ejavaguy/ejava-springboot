package info.ejava.examples.svc.authn.users;

import info.ejava.examples.common.web.RestTemplateLoggingFilter;
import info.ejava.examples.common.web.ServerConfig;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

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
    public RestTemplate anonymousUser(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.requestFactory(
                //used to read the streams twice -- so we can use the logging filter below
                ()->new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .interceptors(new RestTemplateLoggingFilter())
                .build();
        return restTemplate;
    }

    @Bean
    public RestTemplate authnUser(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.requestFactory(
                //used to read the streams twice -- so we can use the logging filter below
                ()->new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .interceptors(new BasicAuthenticationInterceptor("user", "password"),
                        new RestTemplateLoggingFilter())
                .build();
        return restTemplate;
    }
}
