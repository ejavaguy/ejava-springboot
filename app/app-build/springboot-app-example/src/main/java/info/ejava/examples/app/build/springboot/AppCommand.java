package info.ejava.examples.app.build.springboot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AppCommand implements CommandLineRunner {
    public void run(String... args) throws Exception {
        System.out.println("Component code says Hello " + Arrays.asList(args));
    }
}
