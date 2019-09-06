package info.ejava.examples.app.config.logging.factory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("system-out")
public class SystemOutCommand implements CommandLineRunner {
    public void run(String... args) throws Exception {
        System.out.println("System.out message");
    }
}
