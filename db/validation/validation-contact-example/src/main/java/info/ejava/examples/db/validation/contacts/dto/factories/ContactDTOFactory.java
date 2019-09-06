package info.ejava.examples.db.validation.contacts.dto.factories;

import com.github.javafaker.Faker;
import info.ejava.examples.db.validation.contacts.dto.ContactPointDTO;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
public class ContactDTOFactory {
    private static final AtomicInteger nextId = new AtomicInteger(1);
    private final Faker faker = new Faker();
    private final StreetAddressDTOFactory streetFactory =new StreetAddressDTOFactory();

    public static List<String> NAMES = Arrays.asList("Home","Work","Office","Cell","Other");

    public String name() { return NAMES.get(faker.number().numberBetween(0,NAMES.size())); }
    public String email() { return faker.internet().emailAddress(); }
    public String phone() { return faker.phoneNumber().phoneNumber(); }

    public ContactPointDTO make(UnaryOperator<ContactPointDTO>...visitors) {
        final ContactPointDTO result = ContactPointDTO.builder()
                .name(name())
                .email(email())
                .phone(phone())
                .address(streetFactory.make())
                .build();
        Stream.of(visitors).forEach(v->v.apply(result));
        return result;
    }

    public static UnaryOperator<ContactPointDTO> oneUpId = c->{
        c.setId(Integer.valueOf(nextId.getAndAdd(1)).toString());
        return c;
    };
    public static UnaryOperator<ContactPointDTO> noName = c->{
        c.setName(null);
        return c;
    };
    public static UnaryOperator<ContactPointDTO> noAddress = c->{
        c.setAddress(null);
        return c;
    };
    public static UnaryOperator<ContactPointDTO> badEmail = c->{
        c.setEmail(new Faker().lorem().characters(10,15));
        return c;
    };
    public static UnaryOperator<ContactPointDTO> emailToShort = c->{
        c.setEmail(new Faker().lorem().characters(3,5));
        return c;
    };

    public StreetAddressDTOFactory streetBuilder() { return streetFactory; }
    public ContactsListDTOFactory listBuilder() { return new ContactsListDTOFactory(); }

    public class ContactsListDTOFactory {
        public List<ContactPointDTO> make(int min, int max, UnaryOperator<ContactPointDTO>...visitors) {
            return IntStream.range(0, faker.number().numberBetween(min, max))
                    .mapToObj(i-> ContactDTOFactory.this.make(visitors))
                    .collect(Collectors.toList());
        }
    }
}
