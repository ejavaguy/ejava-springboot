package info.ejava.examples.svc.docker.votes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * A test configuration used by remote IT test clients.
 */
@SpringBootConfiguration()
@EnableAutoConfiguration       //needed to setup logging
public class ClientTestConfiguration {
    @Value("${it.server.host:localhost}")
    private String host;
    @Value("${it.server.port:9090}")
    private int port;

    @Bean
    public URI baseUrl() {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(host)
                .port(port)
                .build()
                .toUri();
    }

    @Bean
    public URI votesUrl(URI baseUrl) {
        return UriComponentsBuilder.fromUri(baseUrl).path("api/votes")
                .build().toUri();

    }
    @Bean
    public URI electionsUrl(URI baseUrl) {
        return UriComponentsBuilder.fromUri(baseUrl).path("api/elections")
                .build().toUri();
    }
    @Bean
    public RestTemplate anonymousUser(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder.build();
        return restTemplate;
    }
}
