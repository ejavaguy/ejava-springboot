package info.ejava.examples.app.config.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DuplicatesLogger implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        for (int i=0; i<50; i++) {
            doWork(""+i);
        }
    }

    public void doWork(String job) {
        log.info("starting work for repetition {}", job);
        log.info("finished work for repetition " + job);
    }
}
