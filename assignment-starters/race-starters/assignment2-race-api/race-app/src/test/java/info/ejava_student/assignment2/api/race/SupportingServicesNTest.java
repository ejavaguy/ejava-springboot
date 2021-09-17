package info.ejava_student.assignment2.api.race;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * This is primarily a test/demonstration that the supporting Race
 * and Racer services will be auto-configured when the application
 * is started. If this test passes, then we know the dependencies are
 * correct, Auto-configuration is enabled, and auto-config is working
 * the way it should.
 */
@SpringBootTest(classes={RaceApp.class}, //use only the App class as the configuration
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
//    properties = "debug=true" //turn on conditions report
    )
@Slf4j
public class SupportingServicesNTest {
    //instantiate a default RestTemplate to eliminate test dependencies
    private RestTemplate restTemplate = new RestTemplate();
    private URI baseUrl;

    @BeforeEach
    void init(@LocalServerPort int port) {
        baseUrl = UriComponentsBuilder.fromHttpUrl("http://localhost/api").port(port).build().toUri();
        log.info("baseUrl={}", baseUrl);
    }

    /**
     * Helper method to call the URL and verify a successful response
     * @param url
     * @return response
     */
    private ResponseEntity<String> url_present(URI url) {
        //given
        RequestEntity request = RequestEntity.get(url).build();
        //when
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        //then
        then(response.getStatusCode().is2xxSuccessful()).isTrue();
        return response;
    }

    @Test
    void races_present() {
        //given
        URI url = UriComponentsBuilder.fromUri(baseUrl).path("/races").build().toUri();
        //verify
        url_present(url);
    }

    @Test
    void racers_present() {
        //given
        URI url = UriComponentsBuilder.fromUri(baseUrl).path("/racers").build().toUri();
        //verify
        url_present(url);
    }
}
