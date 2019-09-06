package info.ejava.examples.db.validation.contacts.controller;

import info.ejava.examples.common.dto.JsonUtil;
import info.ejava.examples.common.dto.MessageDTO;
import info.ejava.examples.common.web.ServerConfig;
import info.ejava.examples.db.validation.contacts.NTestConfiguration;
import info.ejava.examples.db.validation.contacts.ValidatingServiceConfiguration;
import info.ejava.examples.db.validation.contacts.ValidatorConfiguration;
import info.ejava.examples.db.validation.contacts.dto.PersonPocDTO;
import info.ejava.examples.db.validation.contacts.dto.factories.PersonPocDTOFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * This test uses a non-validating API and relies on the service to validate all inputs.
 * What we will notice is that vanilla/Spring/AOP method validation will throw a
 * ConstraintViolationException -- which we have to determine is an internal error or
 * a client error if we want a useful HTTP status response.
 */
@SpringBootTest(classes={NTestConfiguration.class,
        ValidatorConfiguration.class,
        ValidatingServiceConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("springboot")
@Slf4j
public class ValidatingServiceNTest {
    @Autowired
    private PersonPocDTOFactory pocDTOFactory;
    @Autowired
    private WebClient wc;
    @Autowired
    private WebTestClient wtc;

    @BeforeEach
    void init(@LocalServerPort int port) {
        ServerConfig serverConfig = new ServerConfig().withPort(port).build();
        wtc = wtc.mutate().baseUrl(serverConfig.getBaseUrl().toString()).build();
    }

    /**
     * This test verifies that when the payload fails validation, the
     * exception thrown will be mapped to a 400/BAD_REQUEST
     */
    @Test
    void identifies_creation_bad_request() {
        //given - an invalid PersonPoc with an ID pre-assigned by caller in the create
        PersonPocDTO personPOC = pocDTOFactory.make(PersonPocDTOFactory.oneUpId); //Create group
        WebTestClient.RequestHeadersSpec<?> request = wtc.post()
                .uri(ContactsController.CONTACTS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(personPOC), PersonPocDTO.class)
                .accept(MediaType.APPLICATION_JSON);
        //when - create called
        WebTestClient.ResponseSpec response = request.exchange();
        //then
        response.expectStatus().is4xxClientError();
        response.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);

        MessageDTO errorMsg = response.expectBody(MessageDTO.class).returnResult().getResponseBody();
        log.info("{}", JsonUtil.instance().marshal(personPOC));
        log.info("{}", JsonUtil.instance().marshal(errorMsg));
        log.info("\n{}", errorMsg.getDescription());
        List<String> errorMsgs = Arrays.asList(errorMsg.getDescription().split("\n"));
        then(errorMsgs).contains(
                "createPOC.person.id: cannot be specified for create"
        );
    }

    @Test
    void identifies_multiple_creation_violations() {
        //given - an invalid PersonPoc with an ID pre-assigned by caller in the create
        PersonPocDTO personPOC = pocDTOFactory.make(PersonPocDTOFactory.oneUpId) //Create group
                .withFirstName(null)
                .withLastName(null)
                .withDob(LocalDate.now().plusYears(1)) //Default Group
                .withContactPoints(Collections.emptyList())
                ;
        WebTestClient.RequestHeadersSpec<?> request = wtc.post()
                .uri(ContactsController.CONTACTS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(personPOC), PersonPocDTO.class)
                .accept(MediaType.APPLICATION_JSON);
        //when - create called
        WebTestClient.ResponseSpec response = request.exchange();
        //then
        response.expectStatus().is4xxClientError()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);

        MessageDTO errorMsg = response.expectBody(MessageDTO.class).returnResult().getResponseBody();
        log.info("{}", JsonUtil.instance().marshal(personPOC));
        log.info("{}", JsonUtil.instance().marshal(errorMsg));
        log.info("\n{}", errorMsg.getDescription());
        List<String> errorMsgs = Arrays.asList(errorMsg.getDescription().split("\n"));
        then(errorMsgs).contains(
                "createPOC.person.id: cannot be specified for create",
                "createPOC.person.dob: must be a past date",
                "createPOC.person: first and/or lastName must be supplied",
                "createPOC.person.contactPoints: must have at least one contact point"
        );
    }

    /**
     * This test verifies that when a request parameter fails validation,
     * the exception thrown will be mapped to a 400/BAD_REQUEST.
     */
    @Test
    void get_identifies_badId_id_in_path() {
        //given
        String badId="1...34";
        WebTestClient.RequestHeadersSpec<?> request = wtc.get()
                .uri(b -> b.path(ContactsController.CONTACT_PATH).build(badId))
                .accept(MediaType.APPLICATION_JSON);
        //when
        WebTestClient.ResponseSpec response = request.exchange();
        //then
        response.expectStatus().is4xxClientError();
        //if this is a 404, then no validation was performed
        response.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);

        MessageDTO errorMsg = response.expectBody(MessageDTO.class).returnResult().getResponseBody();
        log.info("{}", JsonUtil.instance().marshal(errorMsg));
        then(errorMsg.getDescription()).isEqualTo("getPOC.id: must be a valid number");
    }

    @Test
    void internal_error_reported() {
        //given
        int positiveValue = 1;
        WebTestClient.RequestHeadersSpec<?> request = wtc.get()
                .uri(b->b.path(ContactsController.POSITIVE_OR_ZERO_PATH)
                        .queryParam("value", positiveValue)
                        .build())
                .accept(MediaType.APPLICATION_JSON);
        //when
        WebTestClient.ResponseSpec response = request.exchange();
        //then
        response.expectStatus().is5xxServerError();
        response.expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        MessageDTO errorMsg = response.expectBody(MessageDTO.class).returnResult().getResponseBody();
        log.info("{}", JsonUtil.instance().marshal(errorMsg));
        then(errorMsg.getDescription()).isEqualTo("negativeOrZero.value: must be less than or equal to 0");
    }
}
