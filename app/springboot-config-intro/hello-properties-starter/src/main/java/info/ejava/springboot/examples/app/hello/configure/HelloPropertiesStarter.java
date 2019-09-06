package info.ejava.springboot.examples.app.hello.configure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import info.ejava.springboot.examples.app.hello.Hello;
import info.ejava.springboot.examples.app.hello.StdOutHello;

@Configuration
@ConditionalOnClass(StdOutHello.class)
@EnableConfigurationProperties(HelloProperties.class)
public class HelloPropertiesStarter {

    @Bean
    @ConditionalOnMissingBean
    public Hello hello(HelloProperties config) {
        return new StdOutHello(config.getGreeting());
    }
}
