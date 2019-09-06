package info.ejava.examples.db.validation.contacts.bo;

import info.ejava.examples.db.validation.contacts.dto.factories.ContactDTOFactory;
import info.ejava.examples.db.validation.contacts.dto.factories.PersonPocDTOFactory;
import info.ejava.examples.db.validation.contacts.dto.factories.StreetAddressDTOFactory;
import info.ejava.examples.db.validation.contacts.repo.ContactsRepositoryImpl;
import info.ejava.examples.db.validation.contacts.svc.ContactsMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static info.ejava.examples.db.validation.contacts.dto.factories.PersonPocDTOFactory.contactsMany;
import static org.assertj.core.api.BDDAssertions.*;

@Slf4j
public class SortTest {

    ContactsMapper mapper= new ContactsMapper();
    StreetAddressDTOFactory streetFactory = new StreetAddressDTOFactory();
    ContactDTOFactory contactFactory = new ContactDTOFactory();
    PersonPocDTOFactory pocDTOFactory = new PersonPocDTOFactory();
    ContactsRepositoryImpl contactRepo = new ContactsRepositoryImpl();

    @Test
    void streets_are_sorted() {
        //given - a collection of random streets
        List<StreetAddress> streets = streetFactory.listBuilder().make(100, 100).stream()
                .map(str -> mapper.map(str))
                .collect(Collectors.toList());
        log.info("original list sorted={}", areStreetsSorted(streets));
        //when
        Collections.sort(streets);
        log.info("sorted list sorted={}", areStreetsSorted(streets));
        //then
        then(areStreetsSorted(streets)).isTrue();
    }

    @Test
    void empty_street_property_sorts_first() {
        List<StreetAddress> streets = streetFactory.listBuilder().make(3, 3).stream()
                .map(str -> mapper.map(str))
                .collect(Collectors.toList());
        streets.set(1, StreetAddress.builder().build()); //replace street with empty
        log.info("original list sorted={}", areStreetsSorted(streets));
        //when
        Collections.sort(streets);
        log.info("sorted list sorted={}", areStreetsSorted(streets));
        //then
        then(areStreetsSorted(streets)).isTrue();
    }

    boolean areStreetsSorted(List<StreetAddress> streets) {
        for (int i=0; i<streets.size()-1; i++) {
            StreetAddress str1 = streets.get(i);
            StreetAddress str2 = streets.get(i+1);
            if (str1.getStreet()==null && str2.getStreet()!=null) {
                return true; //nulls should be in lead
            } else if (str1.getStreet()!=null && str2.getStreet()==null) {
                return false;
            }

            if (str1.getStreet().compareTo(str2.getStreet()) > 0) {
                return false;
            } else if (str1.getStreet().compareTo(str2.getStreet()) == 0) {
                if (str1.getCity().compareTo(str2.getCity()) > 0) {
                    return false;
                } else if (str1.getCity().compareTo(str2.getCity()) == 0) {
                    if (str1.getState().compareTo(str2.getState()) > 0) {
                        return false;
                    } else if (str1.getState().compareTo(str2.getState()) == 0) {
                        if (str1.getZip().compareTo(str2.getZip()) > 0) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Test
    void contacts_are_sorted() {
        List<ContactPoint> contacts = contactFactory.listBuilder().make(100, 100).stream()
                .map(str -> mapper.map(str))
                .collect(Collectors.toList());

        log.info("original list sorted={}", areContactsSorted(contacts));
        //when
        Collections.sort(contacts);
        log.info("sorted list sorted={}", areContactsSorted(contacts));
        //then
        then(areContactsSorted(contacts)).isTrue();
    }

    @Test
    void empty_contact_property_sorts_first() {
        List<ContactPoint> contacts = contactFactory.listBuilder().make(3, 3).stream()
                .map(str -> mapper.map(str))
                .collect(Collectors.toList());
        contacts.set(1, ContactPoint.builder().build()); //replace street with empty
        log.info("original list sorted={}", areContactsSorted(contacts));
        //when
        Collections.sort(contacts);
        log.info("sorted list sorted={}", areContactsSorted(contacts));
        //then
        then(areContactsSorted(contacts)).isTrue();
    }

    boolean areContactsSorted(List<ContactPoint> streets) {
        for (int i=0; i<streets.size()-1; i++) {
            ContactPoint con1 = streets.get(i);
            ContactPoint con2 = streets.get(i+1);
            if (con1.getEmail()==null && con2.getEmail()!=null) {
                return true;
            } else if (con1.getEmail()!=null && con2.getEmail()==null) {
                return false;
            }

            if (con1.getName().compareTo(con2.getName()) > 0) {
                return false;
            } else if (con1.getName().compareTo(con2.getName()) == 0) {
                if (con1.getEmail().compareTo(con2.getEmail()) > 0) {
                    return false;
                } else if (con1.getEmail().compareTo(con2.getEmail()) == 0) {
                    if (con1.getPhoneNumber().compareTo(con2.getPhoneNumber()) > 0) {
                        return false;
                    } else if (con1.getPhoneNumber().compareTo(con2.getPhoneNumber()) == 0) {
                        if (con1.getAddress().compareTo(con2.getAddress()) > 0) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Test
    void contacts_get_sorted() {
        //given
        PersonPOC poc = mapper.map(pocDTOFactory.make(contactsMany));
        List<ContactPoint> contacts = poc.getContactPoints();
        log.info("original list sorted={}", areContactsSorted(contacts));

        //when
        Collections.sort(contacts);
        log.info("sorted list sorted={}", areContactsSorted(contacts));

        //then
        then(areContactsSorted(contacts)).isTrue();
        contactRepo.onBeforeConvert(poc, "pocs");

    }

    @Test
    void contacts_get_sorted_names() {
        //given
        PersonPOC poc = mapper.map(pocDTOFactory.make(contactsMany));
        //when
        contactRepo.onBeforeConvert(poc, "pocs");
        //then
        then(hasSortedNames(poc.getContactPoints())).isTrue();
    }

    boolean hasSortedNames(Collection<ContactPoint> contactPoints) {
        Map<String, List<ContactPoint>> byName = contactPoints.stream()
                .collect(Collectors.groupingBy(
                            ContactPoint::getName,
                            Collectors.mapping(Function.identity(), Collectors.toList()))
                );

        for (List<ContactPoint> sameName : byName.values()) {
            Collections.sort(sameName);
            ContactPoint previous=null;
            for (ContactPoint cp: sameName) {
                if (previous!=null) {
                    assertThat(cp.getId()).isGreaterThan(previous.getId());
                }
                previous=cp;
            }
        }
        return true;
    }
}
