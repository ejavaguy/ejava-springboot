package info.ejava.examples.svc.docker.votes;

import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.net.URI;
import java.util.Random;

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

    public static File composeFile() {
        File composeFile = new File("./docker-compose.yml");
        Assertions.assertThat(composeFile.exists()).isTrue();
        return composeFile;
    }

    public static DockerComposeContainer testEnvironment() {
        DockerComposeContainer env = new DockerComposeContainer("dockercompose-votes", composeFile())
                .withExposedService("api", 8080)
                .withExposedService("activemq", 61616)
                .withExposedService("postgres", 5432)
                .withExposedService("mongo", 27017);
        return env;
    }

    public static void initProperties(DynamicPropertyRegistry registry, DockerComposeContainer env) {
        registry.add("it.server.port", ()->env.getServicePort("api", 8080));
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

    public static void initProperties(DockerComposeContainer env) {
        System.setProperty("it.server.port", ""+env.getServicePort("api", 8080));
        System.setProperty("spring.data.mongodb.uri", mongoUrl(
                env.getServiceHost("mongo", null),
                env.getServicePort("mongo", 27017)
        ));
        System.setProperty("spring.activemq.broker-url", jmsUrl(
                env.getServiceHost("activemq", null),
                env.getServicePort("activemq", 61616)
        ));
        System.setProperty("spring.datasource.url", jdbcUrl(
                env.getServiceHost("postgres", null),
                env.getServicePort("postgres", 5432)
        ));
    }

    public static void initProperties(ConfigurableApplicationContext ctx, DockerComposeContainer env) {
        TestPropertyValues values = TestPropertyValues.of(
                "it.server.port=" + env.getServicePort("api", 8080),
                "spring.data.mongodb.uri=" + mongoUrl(
                        env.getServiceHost("mongo", null),
                        env.getServicePort("mongo", 27017)),
                "spring.activemq.broker-url=" + jmsUrl(
                        env.getServiceHost("activemq", null),
                        env.getServicePort("activemq", 61616)),
                "spring.datasource.url=" + jdbcUrl(
                        env.getServiceHost("postgres", null),
                        env.getServicePort("postgres", 5432))
        );
        values.applyTo(ctx);
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
