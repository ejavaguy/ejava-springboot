package info.ejava.examples.app.config.propertysource.annotation;

import info.ejava.examples.app.hello.Hello;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppCommand implements CommandLineRunner {
    private final Hello greeter;

    @Value("${app.audience}")
    private String audience;
    
    public AppCommand(Hello greeter) {
        this.greeter = greeter;
    }
    
    public void run(String... args) throws Exception {        
        greeter.sayHello(audience);
    }
}
