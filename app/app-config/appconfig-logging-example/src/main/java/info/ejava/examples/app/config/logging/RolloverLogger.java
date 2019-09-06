package info.ejava.examples.app.config.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("rollover")
@Slf4j
public class RolloverLogger implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        for (int i=0; i<10000000; i++) {
            log.info("i={}", i);
            try { Thread.sleep(1000); } catch (Exception ex){ /*ignore*/}
        }
    }
}

