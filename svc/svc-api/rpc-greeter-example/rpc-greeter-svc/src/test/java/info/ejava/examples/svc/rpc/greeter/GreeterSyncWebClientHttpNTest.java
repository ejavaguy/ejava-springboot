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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

/**
 * This class provides a simple example of using WebClient as a
 * synchronous replacement for RestTemplate.
 */
@SpringBootTest(classes = GreeterApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("springboot") @Tag("greeter")
@Slf4j
public class GreeterSyncWebClientHttpNTest {
    @LocalServerPort
    private int port;
    String baseUrl;
    WebClient webClient;

    @BeforeEach
    public void init() {
        baseUrl = String.format("http://localhost:%d/rpc/greeter", port);
        webClient = WebClient.builder().build();
    }


    @Test
    public void say_hi() {
        //given - a service available at a URL and client access
        String url = String.format("http://localhost:%d/rpc/greeter/sayHi", port);
        WebClient webClient = WebClient.builder().build();

        //when - calling the service
        String greeting = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();

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
        ResponseEntity<String> response = webClient.get()
                .uri(url)
                .retrieve()
                .toEntity(String.class)
                .block();

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
        ResponseEntity<String> response = webClient.get()
            .uri(url)
            .retrieve()
            .toEntity(String.class)
            .block();

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
        WebClientResponseException ex = catchThrowableOfType(
                () -> webClient.get().uri(url).retrieve().toEntity(String.class).block(),
                WebClientResponseException.BadRequest.class);

        //then - we get a bad request
        then(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        then(ex.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
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
        ResponseEntity<String> response = webClient.get()
                .uri(url)
                .retrieve()
                .toEntity(String.class)
                .block();

        //then - we get a valid response
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
        WebClientResponseException ex = catchThrowableOfType(
                () -> webClient.get().uri(url).retrieve().toEntity(String.class).block(),
                WebClientResponseException.UnprocessableEntity.class);

        //then - we get a bad request
        then(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        then(ex.getHeaders().getFirst(HttpHeaders.CONTENT_LOCATION))
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
        WebClientResponseException ex = catchThrowableOfType(
                () -> webClient.get().uri(url).retrieve().toEntity(String.class).block(),
                WebClientResponseException.UnprocessableEntity.class);

        //then - we get a bad request
        then(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        then(ex.getHeaders().getFirst(HttpHeaders.CONTENT_LOCATION))
                .isNull();
        then(ex.getResponseBodyAsString()).isEqualTo("boy named blue?");
    }
}
