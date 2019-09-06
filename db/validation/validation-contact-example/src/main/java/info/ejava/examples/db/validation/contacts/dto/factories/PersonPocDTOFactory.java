package info.ejava.examples.db.validation.contacts.dto.factories;

import com.github.javafaker.Faker;
import info.ejava.examples.db.validation.contacts.dto.PersonPocDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
public class PersonPocDTOFactory {
    private static final AtomicInteger nextId = new AtomicInteger(1);
    private final Faker faker = new Faker();
    private final ContactDTOFactory contactDTOFactory = new ContactDTOFactory();

    public String firstName() { return faker.name().firstName(); }
    public String lastName() { return faker.name().lastName(); }
    public LocalDate dob() { return LocalDate.ofInstant(faker.date()
            .past(40, TimeUnit.DAYS).toInstant(), ZoneOffset.UTC)
            .minusYears(10); //make sure in the past
        }

    public PersonPocDTO make(UnaryOperator<PersonPocDTO>...visitors) {
        final PersonPocDTO result = PersonPocDTO.builder()
                .firstName(firstName())
                .lastName(lastName())
                .dob(dob())
                .contactPoints(contactDTOFactory.listBuilder().make(1,1))
                .build();
        Stream.of(visitors).forEach(v->v.apply(result));
        return result;
    }

    public static UnaryOperator<PersonPocDTO> oneUpId = c->{
        c.setId(Integer.valueOf(nextId.getAndAdd(1)).toString());
        return c;
    };
    public static UnaryOperator<PersonPocDTO> contacts = c->{
        c.setContactPoints(new ContactDTOFactory().listBuilder().make(1,2));
        return c;
    };
    public static UnaryOperator<PersonPocDTO> contactsMany = c->{
        c.setContactPoints(new ContactDTOFactory().listBuilder().make(5,10));
        return c;
    };

    public ContactDTOFactory contactBuilder() { return contactDTOFactory; }
    public PersonPocListDTOFactory listBuilder() { return new PersonPocListDTOFactory(); }

    public class PersonPocListDTOFactory {
        public List<PersonPocDTO> make(int min, int max, UnaryOperator<PersonPocDTO>...visitors) {
            return IntStream.range(0, faker.number().numberBetween(min, max))
                    .mapToObj(i-> PersonPocDTOFactory.this.make(visitors))
                    .collect(Collectors.toList());
        }
    }
}
