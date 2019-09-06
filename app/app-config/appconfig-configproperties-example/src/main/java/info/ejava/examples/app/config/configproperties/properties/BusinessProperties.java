package info.ejava.examples.app.config.configproperties.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@ConfigurationProperties("app.config.business")
@ConstructorBinding
@Data
@Validated
public class BusinessProperties {
    @NotNull
    private final String name;
    @NotNull
    private final String streetAddress;
    @NotNull
    private final String city;
    @NotNull
    private final String state;
    @NotNull
    private final String zipCode;
    private final String notes;

    //https://github.com/spring-projects/spring-boot/issues/18730
    //https://github.com/rzwitserloot/lombok/issues/2275
    public BusinessProperties(String name, String streetAddress, String city, String state, String zipCode, String notes) {
        this.name = name;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.notes = notes;
    }
}
