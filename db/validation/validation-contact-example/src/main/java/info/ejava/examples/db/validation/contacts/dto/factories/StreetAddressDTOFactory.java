package info.ejava.examples.db.validation.contacts.dto.factories;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import info.ejava.examples.db.validation.contacts.dto.StreetAddressDTO;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreetAddressDTOFactory {
    private final Faker faker = new Faker();
    public String street() { return faker.address().streetAddress(); }
    public String city() { return faker.address().city(); }
    public String state() { return faker.address().stateAbbr(); }
    public String zip() { return faker.address().zipCode(); }

    public StreetAddressDTO make(UnaryOperator<StreetAddressDTO>...visitors) {
        Address address = faker.address();
        final StreetAddressDTO result = StreetAddressDTO.builder()
                .street(address.streetAddress())
                .city(address.city())
                .state(address.stateAbbr())
                .zip(address.zipCode())
                .build();
        Stream.of(visitors).forEach(v->v.apply(result));
        return result;
    }

    public static UnaryOperator<StreetAddressDTO> InvalidStreetName = address-> {
            address.setStreet("!@#$%^&*())_");
            return address;
    };

    public StreetAddressesDTOFactory listBuilder() { return new StreetAddressesDTOFactory(); }

    public class StreetAddressesDTOFactory {
        public List<StreetAddressDTO> make(int min, int max, UnaryOperator<StreetAddressDTO>...visitors) {
            return IntStream.range(0, faker.number().numberBetween(min, max))
                    .mapToObj(i-> StreetAddressDTOFactory.this.make(visitors))
                    .collect(Collectors.toList());
        }
    }
}
