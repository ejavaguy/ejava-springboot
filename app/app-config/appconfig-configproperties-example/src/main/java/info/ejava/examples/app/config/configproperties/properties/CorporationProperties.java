package info.ejava.examples.app.config.configproperties.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * This class provides a example of ConfigurationProperties class that uses
 * nested properties.
 */
@ConfigurationProperties("app.config.corp")
@ConstructorBinding
@Data
@Validated
public class CorporationProperties {
    @NotNull
    private final String name;
    @NestedConfigurationProperty //needed for metadata
    @NotNull
    private final AddressProperties address;

    //https://github.com/spring-projects/spring-boot/issues/18730
    //https://github.com/rzwitserloot/lombok/issues/2275
    public CorporationProperties(String name, AddressProperties address) {
        this.name = name;
        this.address = address;
    }
}
