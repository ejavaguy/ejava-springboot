package info.ejava.examples.app.config.propertysource.annotation;

import info.ejava.examples.app.hello.Hello;
import info.ejava.examples.app.hello.stdout.StdOutHello;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:property_source.properties")
public class PropertySourceApp {
    public static final void main(String...args) {
        SpringApplication.run(PropertySourceApp.class, args);
    }
    
    @Bean
    public Hello hello() {
        return new StdOutHello("Application @Bean says Hey");
    }
}
