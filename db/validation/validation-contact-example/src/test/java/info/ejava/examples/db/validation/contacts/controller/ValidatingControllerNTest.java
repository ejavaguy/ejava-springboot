package info.ejava.examples.db.validation.contacts.controller;

import info.ejava.examples.common.dto.JsonUtil;
import info.ejava.examples.common.dto.MessageDTO;
import info.ejava.examples.common.web.ServerConfig;
import info.ejava.examples.common.web.paging.PageableDTO;
import info.ejava.examples.db.validation.contacts.NTestConfiguration;
import info.ejava.examples.db.validation.contacts.ValidatorConfiguration;
import info.ejava.examples.db.validation.contacts.dto.PersonPocDTO;
import info.ejava.examples.db.validation.contacts.dto.factories.PersonPocDTOFactory;
import info.ejava.examples.db.validation.contacts.ValidatingControllerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.BDDAssertions.then;

/**
 * This test case demonstrates validation all at the controller level. The service is
 * blindly accepting inputs.
 */

@SpringBootTest(classes={NTestConfiguration.class,
        ValidatorConfiguration.class,
        ValidatingControllerConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("springboot")
@Slf4j
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ValidatingControllerNTest {
    @Autowired
    private PersonPocDTOFactory pocDTOFactory;
    @Autowired
    private WebClient wc;
    @Autowired
    private WebTestClient wtc;
    private ServerConfig serverConfig;

    @BeforeEach
    void init(@LocalServerPort int port) {
        serverConfig = new ServerConfig().withPort(port).build();
        wtc = wtc.mutate().baseUrl(serverConfig.getBaseUrl().toString()).build();
        wc = wc.mutate().baseUrl(serverConfig.getBaseUrl().toString()).build();
    }

    /**
     * This test verifies that when the payload fails validation, the
     * exception thrown will be mapped to a 422/UNPROCESSABLE_ENTITY
     */
    @Test
    void identifies_creation_unprocessable_entity() {
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
        response.expectStatus().value(v->{if (v <= 299) fail("unexpected success:" + v); });
        response.expectStatus().is4xxClientError();
        response.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        MessageDTO errorMsg = response.expectBody(MessageDTO.class).returnResult().getResponseBody();
        log.info("{}", JsonUtil.instance().marshal(personPOC));
        log.info("{}", JsonUtil.instance().marshal(errorMsg));
        log.info("\n{}", errorMsg.getDescription());
        List<String> errorMsgs = Arrays.asList(errorMsg.getDescription().split("\n"));
        then(errorMsg.getDescription()).contains(
                "id: cannot be specified for create"
        );
        then(errorMsgs).hasSize(1);
    }

    /**
     * This test verifies that when a request parameter fails validation,
     * the exception thrown will be mapped to a 400/BAD_REQUEST.
     */
    @Test
    void get_identifies_badId_path() {
        //given
        String badId="1...34";
        WebTestClient.RequestHeadersSpec<?> request = wtc.delete()
                .uri(b -> b.path(ContactsController.CONTACT_PATH).build(badId))
                .accept(MediaType.APPLICATION_JSON);
        //when
        WebTestClient.ResponseSpec response = request.exchange();
        //then
        response.expectStatus().is4xxClientError();
        //if this is a 200/OK, then no validation was performed
        response.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);

        MessageDTO errorMsg = response.expectBody(MessageDTO.class).returnResult().getResponseBody();
        log.info("{}", JsonUtil.instance().marshal(errorMsg));
        then(errorMsg.getDescription()).isEqualTo("deletePOC.id: must be a number");
    }

    private List<PersonPocDTO> createPOCs(Collection<PersonPocDTO> pocs) {
        List<PersonPocDTO> results = new ArrayList<>(pocs.size());
        for (PersonPocDTO poc : pocs) {
            WebClient.RequestHeadersSpec<?> request = wc.post()
                    .uri(b->b.path(ContactsController.CONTACTS_PATH).build())
                    .body(Mono.just(poc), PersonPocDTO.class)
                    .accept(MediaType.APPLICATION_JSON);
            ResponseEntity<PersonPocDTO> response = request.retrieve().toEntity(PersonPocDTO.class).block();
            then(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            results.add(response.getBody());
        }
        return results;
    }

    private void deleteAllPOCs() {
        WebClient.RequestHeadersSpec<?> request = wc.delete()
                .uri(b->b.path(ContactsController.CONTACTS_PATH).build())
                .accept(MediaType.APPLICATION_JSON);
        ResponseEntity<Void> response = request.retrieve().toEntity(Void.class).block();
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Nested
    class Finder {
        List<PersonPocDTO> pocs;

        @BeforeEach
        void init() {
            if (pocs==null) {
                pocs = createPOCs(pocDTOFactory.listBuilder().make(5, 5));
                log.info("created pocs\n{}", StringUtils.join(pocs,"\n"));
            }
        }
        @AfterEach
        void cleanup() {
            deleteAllPOCs();
        }
        
        private WebTestClient.RequestHeadersSpec<?> given_request(URI uri, PersonPocDTO probe) {
            return wtc.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(probe), PersonPocDTO.class)
                    .accept(MediaType.APPLICATION_JSON);
        }

        @Test
        void bad_paging_returns_bad_request() {
            //given
            PersonPocDTO probe = PersonPocDTO.builder().build();

            URI uri = UriComponentsBuilder.fromUri(serverConfig.getBaseUrl())
                    .path(ContactsController.EXAMPLE_CONTACTS_PATH)
                    .queryParam(PageableDTO.PAGE_NUMBER,"-1")
                    .queryParam(PageableDTO.PAGE_SIZE,"0")
                    .build().toUri();
            WebTestClient.RequestHeadersSpec<?> request = given_request(uri, probe);
            //when
            WebTestClient.ResponseSpec response = request.exchange();
            //then
            response.expectStatus().is4xxClientError();
            response.expectStatus().isBadRequest();
            MessageDTO errorMsg = response.expectBody(MessageDTO.class).returnResult().getResponseBody();
            log.info("{}", JsonUtil.instance().marshal(errorMsg));
            List<String> errorMsgs = Arrays.asList(errorMsg.getDescription().split("\n"));
            then(errorMsgs).contains(
                    "findPocsByExample.pageNumber: must be greater than or equal to 0",
                    "findPocsByExample.pageSize: must be greater than 0"
            );
        }
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
