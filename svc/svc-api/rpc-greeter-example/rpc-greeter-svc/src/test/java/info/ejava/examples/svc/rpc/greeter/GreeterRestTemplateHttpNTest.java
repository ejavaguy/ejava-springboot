package info.ejava.examples.svc.rpc.greeter;

import info.ejava.examples.svc.rpc.GreeterApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

/**
 * This class provides a simple example of using the synchronous
 * RestTemplate client.
 */
@SpringBootTest(classes = GreeterApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("springboot") @Tag("greeter")
@Slf4j
public class GreeterRestTemplateHttpNTest {
    @LocalServerPort
    private int port;
    String baseUrl;
    RestTemplate restTemplate;

    @BeforeEach
    public void init() {
        baseUrl = String.format("http://localhost:%d/rpc/greeter", port);
        restTemplate = new RestTemplate();
    }

    @Test
    public void say_hi() {
        //given - a service available at a URL and client access
        String url = String.format("http://localhost:%d/rpc/greeter/sayHi", port);
        RestTemplate restTemplate = new RestTemplate();

        //when - calling the service
        String greeting = restTemplate.getForObject(url, String.class);

        //then - we get a greeting response
        then(greeting).isEqualTo("hi");
    }

    @Test
    public void say_greeting() {
        //given - a service available to provide a greeting
        URI url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/say/{greeting}")
                .queryParam("name", "{name}")
                .build("hello", "jim");

        //when - asking for that greeting
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        //then - response be successful with expected greeting
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).startsWith("text/plain");
        then(response.getBody()).isEqualTo("hello, jim");
    }

    @Test
    public void no_boom() {
        //given - a URL with all required properties
        URI url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .replacePath("/rpc/greeter/boom")
                .queryParam("value", "whatever")
                .build().toUri();
        //when - calling the service
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        //then - no error
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isEqualTo("worked?");
    }

    @Test
    public void boom() {
        //given - a URL with a missing required query param
        URI url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .replacePath("/rpc/greeter/boom")
                .build().toUri();
        //when - calling the service
        HttpClientErrorException ex = catchThrowableOfType(
                ()->restTemplate.getForEntity(url, String.class),
                HttpClientErrorException.BadRequest.class);

        //then - we get a bad request
        then(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        then(ex.getResponseHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        log.info("{}", ex.getResponseBodyAsString());
    }

    @Test
    public void boy() {
        //given - a URL with a missing required query param
        URI url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/boys")
                .queryParam("name", "jim")
                .build().toUri();
        //when - calling the service
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        //then - we get a bad request
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getHeaders().getFirst(HttpHeaders.CONTENT_LOCATION))
                .isEqualTo(url.toString());
        then(response.getBody()).isEqualTo("hello jim, how do you do?");
    }

    @Test
    public void boy_blue() {
        //given - a URL with a missing required query param
        URI url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/boys")
                .queryParam("name", "blue")
                .build().toUri();
        //when - calling the service
        HttpClientErrorException ex = catchThrowableOfType(
                ()->restTemplate.getForEntity(url, String.class),
                HttpClientErrorException.UnprocessableEntity.class);

        //then - we get a bad request
        then(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        then(ex.getResponseHeaders().getFirst(HttpHeaders.CONTENT_LOCATION))
                .isNull();
        then(ex.getResponseBodyAsString()).isEqualTo("boy named blue?");
    }

    @Test
    public void boy_blue_with_exception_handler() {
        //given - a URL with a missing required query param
        URI url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/boys/throws") //<== new URI
                .queryParam("name", "blue")
                .build().toUri();
        //when - calling the service
        HttpClientErrorException ex = catchThrowableOfType(
                ()->restTemplate.getForEntity(url, String.class),
                HttpClientErrorException.UnprocessableEntity.class);

        //then - we get a bad request
        then(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        then(ex.getResponseHeaders().getFirst(HttpHeaders.CONTENT_LOCATION))
                .isNull();
        then(ex.getResponseBodyAsString()).isEqualTo("boy named blue?");
    }
}
