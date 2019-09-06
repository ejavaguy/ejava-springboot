package info.ejava.examples.app.config.configproperties.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.validation.constraints.NotNull;

@Data
@ConstructorBinding
public class AddressProperties {
    private final String street;
    @NotNull
    private final String city;
    @NotNull
    private final String state;
    @NotNull
    private final String zip;

    //https://github.com/spring-projects/spring-boot/issues/18730
    //https://github.com/rzwitserloot/lombok/issues/2275
    public AddressProperties(String street, String city, String state, String zip) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }
}
