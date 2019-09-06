package info.ejava.examples.app.config.configproperties.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.validation.constraints.NotNull;

@ConfigurationProperties("user")
@ConstructorBinding
@Data
public class UserProperties {
    @NotNull
    private final String name;
    @NotNull
    private final String home;
    @NotNull
    private final String timezone;

    //https://github.com/spring-projects/spring-boot/issues/18730
    //https://github.com/rzwitserloot/lombok/issues/2275
    public UserProperties(String name, String home, String timezone) {
        this.name = name;
        this.home = home;
        this.timezone = timezone;
    }
}
