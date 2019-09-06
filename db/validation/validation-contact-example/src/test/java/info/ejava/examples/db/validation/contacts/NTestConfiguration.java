package info.ejava.examples.db.validation.contacts;

import info.ejava.examples.common.web.RestTemplateLoggingFilter;
import info.ejava.examples.common.web.ServerConfig;
import info.ejava.examples.common.webflux.WebClientLoggingFilter;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@SpringBootConfiguration
@ComponentScan(basePackageClasses = ValidationContactsApp.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
            classes = {ValidationContactsApp.class,
                    ContactsConfiguration.class,
                    ValidatingControllerConfiguration.class,
                    ValidatingServiceConfiguration.class,
                    NullPOCConfiguration.class,
                    ManualValidationConfiguration.class}))
@EnableAutoConfiguration
@EnableMongoRepositories
public class NTestConfiguration {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .filter(WebClientLoggingFilter.requestFilter())
                .filter(WebClientLoggingFilter.responseFilter())
                .exchangeStrategies(ExchangeStrategies.builder().codecs(conf->{
                    conf.defaultCodecs().jaxb2Encoder(new Jaxb2XmlEncoder());
                    conf.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder());
                }).build())
                .build();
    }

    public static WebTestClient webTestClient(Integer port) {
        ServerConfig serverConfig = new ServerConfig();
        if (port!=null) {
            serverConfig.withPort(port);
        }
        serverConfig.build();
        return WebTestClient.bindToServer()
                .baseUrl(serverConfig.getBaseUrl().toString())
                .filter(WebClientLoggingFilter.requestFilter())
                .filter(WebClientLoggingFilter.responseFilter())
                .codecs(conf->{
                    conf.defaultCodecs().jaxb2Encoder(new Jaxb2XmlEncoder());
                    conf.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder());
                })
                .responseTimeout(Duration.ofDays(1))
                .build();
    }

    @Bean
    public WebTestClient webTestClient() {
        return webTestClient(null);
    }


    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.requestFactory(
                //used to read the streams twice -- so we can use the logging filter below
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

    @Configuration
    @ConditionalOnProperty(prefix = "spring.data.mongodb", name = "uri", matchIfMissing = false)
    @EnableAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
    public class DisableEmbeddedMongoConfiguration {
    }
}
