package info.ejava.examples.app.config.beanfactory;

import info.ejava.examples.app.hello.Hello;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppCommand implements CommandLineRunner {
    private final Hello greeter;

    public AppCommand(Hello greeter) {
        this.greeter = greeter;
    }
    
    public void run(String... args) throws Exception {        
        greeter.sayHello("World");
    }
}
