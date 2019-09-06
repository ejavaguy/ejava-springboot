package info.ejava.examples.app.hello;

import info.ejava.examples.app.hello.stdout.StdOutHello;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(StdOutHello.class)
@EnableConfigurationProperties(HelloProperties.class)
public class HelloAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public Hello hello(HelloProperties helloProperties) {
        return new StdOutHello(helloProperties.getGreeting());
    }
}
