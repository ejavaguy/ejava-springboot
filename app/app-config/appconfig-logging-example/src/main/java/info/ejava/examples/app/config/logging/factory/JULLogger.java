package info.ejava.examples.app.config.logging.factory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class JULLogger implements CommandLineRunner {
    private static final Logger log = Logger.getLogger(JULLogger.class.getName());

    @Override
    public void run(String... args) throws Exception {
        log.info("Java Util logger message");
    }
}
