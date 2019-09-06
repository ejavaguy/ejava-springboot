package info.ejava.examples.app.config.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@Slf4j
public class MDCLogger implements CommandLineRunner {
    private static final String[] USERS = new String[]{"jim", "joe", "mary"};
    private static final SecureRandom r = new SecureRandom();

    @Override
    public void run(String... args) throws Exception {
        for (int i=0; i<5; i++) {
            String user = USERS[r.nextInt(USERS.length)];
            MDC.put("user", user);
            MDC.put("requestId", Integer.toString(r.nextInt(99999)));
            doWork();
            MDC.clear();
            doWork();
        }
    }

    public void doWork() {
        log.info("starting work");
        log.info("finished work");
    }
}
