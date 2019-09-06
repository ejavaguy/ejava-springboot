package info.ejava.examples.app.config.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MarkerLogger implements CommandLineRunner {
    private static final Marker ALARM = MarkerFactory.getMarker("ALARM");

    @Override
    public void run(String... args) throws Exception {
        log.warn("non-alarming warning statement");
        log.warn(ALARM,"alarming statement");
    }
}
