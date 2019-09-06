package info.ejava.examples.app.hello;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@ConfigurationProperties("hello")
@Data
@Validated
public class HelloProperties {
    @NotNull
    private String greeting = "HelloProperties default greeting says Hola!";
}
