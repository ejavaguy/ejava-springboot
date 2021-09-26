package info.ejava.assignments.security.race.security;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Collections;
import java.util.List;

@Value
@ConstructorBinding
public class AccountProperties {
    private final String username;
    private final String password;
    private List<String> authorities;

    public List<String> getAuthorities() {
        return null!=authorities ? authorities : Collections.emptyList();
    }
}
