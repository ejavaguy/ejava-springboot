package info.ejava.springboot.examples.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import info.ejava.springboot.examples.app.hello.Hello;

import java.util.Arrays;

@Component
public class AppCommand implements CommandLineRunner {
    private final Hello greeter;
    
    public AppCommand(Hello greeter) {
        this.greeter = greeter;
    }
    
    public void run(String... args) throws Exception {
        System.out.println("args=" + Arrays.asList(args));
        greeter.sayHello("World");
    }
}
