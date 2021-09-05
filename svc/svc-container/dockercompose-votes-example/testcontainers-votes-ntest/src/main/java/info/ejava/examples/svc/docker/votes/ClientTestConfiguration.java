package info.ejava.examples.svc.docker.votes;

import org.apache.commons.compress.utils.IOUtils;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

/**
 * A test configuration used by remote IT test clients.
 */
@SpringBootConfiguration()
@EnableConfigurationProperties //used to set it.server properties
@EnableAutoConfiguration       //needed to setup logging
public class ClientTestConfiguration {
    @Value("${it.server.host:localhost}")
    private String host;
    @Value("${it.server.port:9090}")
    private int port;

    public static File composeFile() {
        Path targetPath = Paths.get("target/docker-compose-votes.yml");
        try (InputStream is = ClientTestConfiguration.class.getResourceAsStream("/docker-compose-votes.yml")) {
            Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Assertions.fail("error creating source Docker Compose file", ex);

        }
        return targetPath.toFile();
    }

    public static DockerComposeContainer testEnvironment() {
        return new DockerComposeContainer("testcontainers-votes", composeFile())
                //add a random API port# in the event that variable is uncommented
                .withExposedService("api", 8080)
                .withExposedService("activemq", 61616)
                .withExposedService("postgres", 5432)
                .withExposedService("mongo", 27017)
                .withLocalCompose(true);
    }

    public static void initProperties(DynamicPropertyRegistry registry, DockerComposeContainer env) {
        registry.add("it.server.port", ()->env.getServicePort("api", 8080));
        registry.add("it.server.host", ()->env.getServiceHost("api", null));
        registry.add("spring.data.mongodb.uri",()-> mongoUrl(
                env.getServiceHost("mongo", null),
                env.getServicePort("mongo", 27017)
            ));
        registry.add("spring.activemq.broker-url", ()->jmsUrl(
                env.getServiceHost("activemq", null),
                env.getServicePort("activemq", 61616)
        ));
        registry.add("spring.datasource.url",()->jdbcUrl(
                env.getServiceHost("postgres", null),
                env.getServicePort("postgres", 5432)
            ));
    }
    public static String mongoUrl(String host, int port) {
        return String.format("mongodb://admin:secret@%s:%d/votes_db?authSource=admin", host, port);
    }
    public static String jmsUrl(String host, int port) {
        return String.format("tcp://%s:%s", host, port);
    }
    public static String jdbcUrl(String host, int port) {
        return String.format("jdbc:postgresql://%s:%d/postgres", host, port);
    }


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
