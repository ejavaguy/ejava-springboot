package info.ejava.examples.app.hello;

import info.ejava.examples.app.hello.stdout.StdOutHello;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(StdOutHello.class)
@ConditionalOnResource(resources = "file:./hello.properties")
@AutoConfigureBefore(HelloAutoConfiguration.class)
public class HelloResourceAutoConfiguration {
    @Bean
    @Primary
    public Hello resourceHello() {
        return new StdOutHello("hello.properties exists says hello");
    }
}
