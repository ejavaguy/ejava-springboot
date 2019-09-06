package info.ejava.examples.app.config.logging.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExceptionExample implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {

        try {
            log.info("calling iThrowException");
            iThrowException();
        } catch (Exception ex) {
            log.warn("caught exception", ex);
            log.warn("caught exception {} {}", "p1","p2", ex);
        }
    }

    private void iThrowException() throws Exception {
        throw new Exception("example exception");
    }
}
