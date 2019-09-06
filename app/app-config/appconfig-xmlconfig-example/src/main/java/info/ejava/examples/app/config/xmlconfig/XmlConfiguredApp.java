package info.ejava.examples.app.config.xmlconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath:contexts/applicationContext.xml"})
public class XmlConfiguredApp {
    public static final void main(String...args) {
        SpringApplication.run(XmlConfiguredApp.class, args);
    }
}
