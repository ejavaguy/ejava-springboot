package info.ejava.examples.app.config.configproperties;

import info.ejava.examples.app.config.configproperties.properties.PersonProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
//@EnableConfigurationProperties(CarProperties.class)
@ConfigurationPropertiesScan
public class ConfigurationPropertiesApp {
    public static final void main(String...args) {
        SpringApplication.run(ConfigurationPropertiesApp.class, args);
    }

//    @Bean
//    @ConfigurationProperties("app.config.car")
//    public CarProperties carProperties() {
//        return new CarProperties();
//    }

    @Bean
    @ConfigurationProperties("owner")
    public PersonProperties ownerProps() {
        return new PersonProperties();
    }

    @Bean
    @ConfigurationProperties("manager")
    //@Manager
    public PersonProperties managerProps() {
        return new PersonProperties();
    }
}
