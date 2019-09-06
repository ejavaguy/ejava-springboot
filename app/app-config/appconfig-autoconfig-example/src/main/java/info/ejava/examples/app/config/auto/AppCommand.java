package info.ejava.examples.app.config.auto;

import info.ejava.examples.app.hello.Hello;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppCommand implements CommandLineRunner {
    private final Hello greeter;

    public void run(String... args) throws Exception {        
        greeter.sayHello("World");
    }
}
