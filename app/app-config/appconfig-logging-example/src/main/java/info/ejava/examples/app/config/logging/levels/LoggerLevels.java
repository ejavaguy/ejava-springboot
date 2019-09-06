package info.ejava.examples.app.config.logging.levels;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggerLevels implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        log.trace("trace message");
        log.debug("debug message");
        log.info("info message");
        log.warn("warn message");
        log.error("error message");
    }
}
