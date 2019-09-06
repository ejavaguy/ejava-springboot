package info.ejava.examples.app.config.configproperties.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * This class provides a example of ConfigurationProperties class that uses
 * a constructor and final variable(s) to create a read-only property class.
 */
@ConfigurationProperties("app.config.boat")
@Validated
public class BoatProperties {
    @NotNull
    private final String name;

    @ConstructorBinding
    public BoatProperties(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "BoatProperties{name='" + name + "\'}";
    }
}
