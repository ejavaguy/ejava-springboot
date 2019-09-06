package info.ejava.examples.app.config.logging.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DeclaredLogger implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DeclaredLogger.class);

    @Override
    public void run(String... args) throws Exception {
        log.info("declared SLF4J logger message");

//        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
//        StatusPrinter.print(lc);
//        LoggerFactory.getLogger("root").info("xxx");
    }
}
