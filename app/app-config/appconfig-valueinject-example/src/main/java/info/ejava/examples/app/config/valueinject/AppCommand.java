package info.ejava.examples.app.config.valueinject;

import info.ejava.examples.app.hello.Hello;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppCommand implements CommandLineRunner {
    private final Hello greeter;

//    @Value("${app.audience:Default World}")
//    private String audience;
//    public AppCommand(Hello greeter) {
//        this.greeter = greeter;
//    }

    private final String audience;
    public AppCommand(Hello greeter,
                      @Value("${app.audience:Default World}") String audience) {
        this.greeter = greeter;
        this.audience = audience;
    }

    public void run(String... args) throws Exception {        
        greeter.sayHello(audience);
    }
}
