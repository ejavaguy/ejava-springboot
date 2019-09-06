package info.ejava.examples.svc.authn.authcfg;

import info.ejava.examples.common.web.ServerConfig;
import info.ejava.examples.svc.authn.AuthConfigExampleApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes={AuthConfigExampleApp.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "test=true")
public class AuthnRestTemplateNTest {
    @Autowired
    private RestTemplate anonymousUser;
    @Autowired
    private RestTemplate authnUser;

    private URI baseUrl;
    private URI anonymousUrl;
    private URI authnUrl;

    @BeforeEach
    public void setUp(@LocalServerPort int port) {
        ServerConfig serverConfig = new ServerConfig().withPort(port).build();
        baseUrl = serverConfig.getBaseUrl();
        anonymousUrl = UriComponentsBuilder.fromUri(baseUrl).path("/api/anonymous/hello").build().toUri();
        authnUrl = UriComponentsBuilder.fromUri(baseUrl).path("/api/authn/hello").build().toUri();
    }

    @Test
    public void anonymous_can_call_unauthenticated() {
        //given a URL to an endpoint that accepts anonymous calls
        URI url = UriComponentsBuilder.fromUri(anonymousUrl).queryParam("name", "jim").build().toUri();

        //when called with no identity
        ResponseEntity<String> response = anonymousUser.getForEntity(url, String.class);

        //then expected results returned
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isEqualTo("hello, jim :caller=(null)");
    }

    @Test
    public void anonymous_cannot_call_authenticated() {
        //given a URL to an endpoint that accepts only authenticated calls
        URI url = UriComponentsBuilder.fromUri(authnUrl).queryParam("name", "jim").build().toUri();

        //when called with no identity
        HttpClientErrorException ex = catchThrowableOfType(
                ()-> anonymousUser.getForEntity(url, String.class),
                HttpClientErrorException.Unauthorized.class);

        //then expected results returned
        then(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void user_can_call_authenticated() {
        //given a URL to an endpoint that accepts only authenticated calls
        URI url = UriComponentsBuilder.fromUri(authnUrl).queryParam("name", "jim").build().toUri();

        //when called with an authenticated identity
        ResponseEntity<String> response = authnUser.getForEntity(url, String.class);

        //then expected results returned
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(response.getBody()).isEqualTo("hello, jim :caller=user");
    }
}
