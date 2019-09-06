package info.ejava.examples.app.config.configproperties.properties;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

//@ConfigurationProperties("???") multiple prefixes mapped
@Data
@Validated
public class PersonProperties {
    @NotNull
    private String name;
    @NestedConfigurationProperty
    @NotNull
    private AddressProperties address;
}
