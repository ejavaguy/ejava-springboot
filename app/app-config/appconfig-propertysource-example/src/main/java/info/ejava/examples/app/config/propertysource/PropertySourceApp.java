package info.ejava.examples.app.config.propertysource;

import info.ejava.examples.app.hello.Hello;
import info.ejava.examples.app.hello.stdout.StdOutHello;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PropertySourceApp {
    public static final void main(String...args) {
        SpringApplication.run(PropertySourceApp.class, args);
    }
    
    @Bean
    public Hello hello() {
        return new StdOutHello("Application @Bean says Hey");
    }
}
