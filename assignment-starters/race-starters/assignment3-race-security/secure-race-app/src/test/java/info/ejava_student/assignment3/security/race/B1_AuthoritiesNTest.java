package info.ejava_student.assignment3.security.race;

import info.ejava.assignments.security.race.config.AuthorizationTestHelperConfiguration;
import info.ejava.assignments.security.race.security.AccountProperties;
import info.ejava.assignments.security.race.security.RaceAccounts;
import info.ejava.examples.common.web.ServerConfig;
import info.ejava_student.assignment3.security.race.security.SecurityController;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes={SecureRaceApp.class, AuthorizationTestHelperConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "authorities"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Part B1: Authorities")
@Disabled("TODO")
public class B1_AuthoritiesNTest {
    @Autowired
    RestTemplate anonymousUser;
    @Autowired
    @Qualifier("userMap")
    private Map<String, RestTemplate> authnUsers;
    @Autowired
    private RaceAccounts accounts;
    private URI authoritiesUrl;

    @BeforeEach
    void init(@LocalServerPort int port) {
        ServerConfig serverConfig = new ServerConfig().withPort(port).build();
        authoritiesUrl = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                .path(SecurityController.AUTHORITIES_PATH)
                .build().toUri();
    }

    Stream<Arguments> all_authorities() {
        return accounts.getAccounts().stream()
                .flatMap(account->account.getAuthorities().stream())
                .collect(Collectors.toSet()) //dedup
                .stream()
                .map(authority->Arguments.of(authority));
    }

    @ParameterizedTest
    @MethodSource("all_authorities")
    @Disabled("TODO")
    void anonymous_has_no_authorities(String authority) {
        //given
        URI url = UriComponentsBuilder.fromUri(authoritiesUrl)
                .queryParam("authority", authority)
                .build().toUri();
        RequestEntity request = RequestEntity.get(url).build();
        //when
        ResponseEntity<String> response = anonymousUser.exchange(request, String.class);
        //then
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(Boolean.valueOf(response.getBody())).isFalse();
    }


    Stream<Arguments> accounts() {
        List<Arguments> accountAuthority = new ArrayList<>();
        for (AccountProperties account: accounts.getAccounts()) {
            for (String authority : account.getAuthorities()) {
                RestTemplate restTemplate = authnUsers.get(account.getUsername());
                accountAuthority.add(Arguments.of(account.getUsername(), authority, restTemplate));
            }
        }
        return accountAuthority.stream();
    }

    @ParameterizedTest
    @MethodSource("accounts")
    @Disabled("TODO")
    void user_has_authorities(String username, String authority, RestTemplate restTemplate) {
        //given
        URI url = UriComponentsBuilder.fromUri(authoritiesUrl)
                .queryParam("authority", authority)
                .build().toUri();
        RequestEntity request = RequestEntity.get(url).build();
        //when
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        //then
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(Boolean.valueOf(response.getBody())).isTrue();
    }
}
