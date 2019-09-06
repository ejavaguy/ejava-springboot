package info.ejava.examples.db.validation.contacts;

import info.ejava.examples.db.validation.contacts.dto.PersonPocDTO;
import info.ejava.examples.db.validation.contacts.dto.factories.PersonPocDTOFactory;
import info.ejava.examples.db.validation.contacts.svc.PocService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;

import static info.ejava.examples.db.validation.contacts.dto.factories.PersonPocDTOFactory.contactsMany;

@SpringBootApplication
@EnableMongoRepositories
@Slf4j
public class ValidationContactsApp {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ValidationContactsApp.class);
        app.setDefaultProperties(Collections.singletonMap("spring.profiles.default", "mongodb"));
        app.run(args);
    }

    @Component
    public class Init implements CommandLineRunner {
        @Autowired
        private PocService pocService;
        @Autowired
        private PersonPocDTOFactory dtoFactory;

        @Override
        public void run(String... args) throws Exception {
            List<PersonPocDTO> pocs = dtoFactory.listBuilder().make(10, 10, contactsMany);
            for (PersonPocDTO poc: pocs) {
                try {
                    pocService.createPOC(poc);
                } catch (ConstraintViolationException ex) {
                    log.warn("{}", poc, ex);
                }
            }
        }
    }
}
