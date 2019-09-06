package info.ejava.examples.app.config.logging;

//import ch.qos.logback.classic.LoggerContext;
//import ch.qos.logback.core.util.StatusPrinter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class LoggerExampleApp {
    public static final void main(String...args) {
//        for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
//            System.out.println(entry.getKey() + "=" + entry.getValue());
//        }
//        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
//        StatusPrinter.print(lc);

        SpringApplication.run(LoggerExampleApp.class, args);

        //lc = (LoggerContext) LoggerFactory.getILoggerFactory();
//        StatusPrinter.print(lc);
    }
}
