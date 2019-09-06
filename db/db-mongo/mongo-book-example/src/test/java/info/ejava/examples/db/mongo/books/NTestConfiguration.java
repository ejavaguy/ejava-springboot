package info.ejava.examples.db.mongo.books;

import info.ejava.examples.common.web.RestTemplateLoggingFilter;
import info.ejava.examples.common.webflux.WebClientLoggingFilter;
import info.ejava.examples.db.mongo.books.dto.BookDTOFactory;
import info.ejava.examples.db.mongo.books.repo.BooksRepository;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@SpringBootConfiguration
@ComponentScan(basePackageClasses = MongoDBBooksApp.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
            classes = {MongoDBBooksApp.class, BooksConfiguration.class}))
@EnableAutoConfiguration
@EnableMongoRepositories(basePackageClasses = {BooksRepository.class})
public class NTestConfiguration {
    @Bean
    public BookDTOFactory dtoFactory() {
        return new BookDTOFactory();
    }

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
