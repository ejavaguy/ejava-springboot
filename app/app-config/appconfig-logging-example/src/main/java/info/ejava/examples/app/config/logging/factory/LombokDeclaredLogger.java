package info.ejava.examples.app.config.logging.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LombokDeclaredLogger implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        log.info("lombok declared SLF4J logger message");
    }
}
