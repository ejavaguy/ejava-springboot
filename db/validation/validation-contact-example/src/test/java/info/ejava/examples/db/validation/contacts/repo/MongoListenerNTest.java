package info.ejava.examples.db.validation.contacts.repo;

import info.ejava.examples.db.validation.contacts.MongoConfiguration;
import info.ejava.examples.db.validation.contacts.NTestConfiguration;
import info.ejava.examples.db.validation.contacts.ValidatorConfiguration;
import info.ejava.examples.db.validation.contacts.bo.PersonPOC;
import info.ejava.examples.db.validation.contacts.dto.factories.PersonPocDTOFactory;
import info.ejava.examples.db.validation.contacts.svc.ContactsMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.mapping.event.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.*;

@SpringBootTest(classes = {NTestConfiguration.class,
        ValidatorConfiguration.class
})
@Tag("springboot")
@Slf4j
public class MongoListenerNTest {
    @Component
    @Getter
    public static class MongoTestListener extends AbstractMongoEventListener {
        List<MongoMappingEvent> events=new ArrayList<>();

        @Override
        public void onBeforeConvert(BeforeConvertEvent event) {
            events.add(event);
            super.onBeforeConvert(event);
        }

        @Override
        public void onBeforeDelete(BeforeDeleteEvent event) {
            events.add(event);
            super.onBeforeDelete(event);
        }

        void clearEvents() { events.clear(); }
    }

    @Autowired
    private MongoTestListener testListener;
    @Autowired
    private PersonPocDTOFactory pocDTOFactory;
    @Autowired
    private ContactsMapper mapper;
    @Autowired
    private ContactsRepository contactsRepository;

    @AfterEach
    void cleanup() {
        contactsRepository.deleteAll();
    }

    @Test
    void catch_save_event() {
        //given
        PersonPOC poc = mapper.map(pocDTOFactory.make());
        testListener.clearEvents();
        //when
        contactsRepository.save(poc);
        //then
        then(testListener.getEvents()).isNotEmpty();
        then(testListener.getEvents()).hasSize(1);
        log.info("{}", testListener.getEvents());
        MongoMappingEvent event = testListener.getEvents().iterator().next();
        then(event).isInstanceOf(BeforeConvertEvent.class);
    }

    @Test
    void catch_delete_event() {
        //given
        PersonPOC poc = mapper.map(pocDTOFactory.make());
        contactsRepository.save(poc);
        testListener.clearEvents();
        //when
        contactsRepository.delete(poc);
        //then
        then(testListener.getEvents()).isNotEmpty();
        then(testListener.getEvents()).hasSize(1);
        log.info("{}", testListener.getEvents());
        MongoMappingEvent event = testListener.getEvents().iterator().next();
        then(event).isInstanceOf(BeforeDeleteEvent.class);
    }
}
