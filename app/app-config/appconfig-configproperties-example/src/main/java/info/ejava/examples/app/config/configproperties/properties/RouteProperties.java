package info.ejava.examples.app.config.configproperties.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@ConfigurationProperties("app.config.route")
@ConstructorBinding
@Data
@Validated
public class RouteProperties {
    @NotNull
    private String name;
    @NestedConfigurationProperty
    @NotNull
    @Size(min = 1)
    private List<AddressProperties> stops;

    //https://github.com/spring-projects/spring-boot/issues/18730
    //https://github.com/rzwitserloot/lombok/issues/2275
    public RouteProperties(String name, List<AddressProperties> stops) {
        this.name = name;
        this.stops = stops;
    }
}
