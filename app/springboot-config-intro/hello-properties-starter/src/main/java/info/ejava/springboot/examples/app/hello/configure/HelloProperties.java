package info.ejava.springboot.examples.app.hello.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("hello")
public class HelloProperties {
    /**
     * Set this value to what you want the Hello service to say when greeting.
     */
    private String greeting="Starter properties says Aloha";

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
}
