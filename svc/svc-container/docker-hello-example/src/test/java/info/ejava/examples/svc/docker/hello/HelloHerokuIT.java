package info.ejava.examples.svc.docker.hello;

import info.ejava.examples.common.web.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.BDDAssertions.*;

@SpringBootTest(classes=ClientTestConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles({"test","heroku"})
@Slf4j
public class HelloHerokuIT {
    @Autowired
    private RestTemplate anonymousUser;
    @Autowired
    private RestTemplate authnUser;
    @Autowired
    private String authnUsername;
    private UriComponentsBuilder helloUrl;

    @BeforeEach
    void init(@Autowired URI baseUrl) {
        log.info("baseUrl={}", baseUrl);
        helloUrl = UriComponentsBuilder.fromUri(baseUrl).path("api/hello")
                .queryParam("name","{name}");
    }

    @Test
    void can_contact_server() {
        //given
        String name="jim";
        URI url = helloUrl.build(name);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        //when
        ResponseEntity<String> response = anonymousUser.exchange(request, String.class);
        //then
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isEqualTo("hello, " + name);
    }

    @Test
    void can_authenticate_with_server() {
        //given
        String name="jim";
        URI url = helloUrl.build(name);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        //when
        ResponseEntity<String> response = authnUser.exchange(request, String.class);
        //then
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isEqualTo("hello, " + name + " (from " + authnUsername + ")");
    }
}
