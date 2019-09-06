package info.ejava.examples.app.config.configproperties.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * This class provides a example of ConfigurationProperties class that uses
 * Lombok to generate boilerplate Java bean methods.
 */
@ConfigurationProperties("app.config.company")
@ConstructorBinding
@Data
@Validated
public class CompanyProperties {
    @NotNull
    private final String name;

    //https://github.com/spring-projects/spring-boot/issues/18730
    //https://github.com/rzwitserloot/lombok/issues/2275
    public CompanyProperties(String name) {
        this.name = name;
    }
}
