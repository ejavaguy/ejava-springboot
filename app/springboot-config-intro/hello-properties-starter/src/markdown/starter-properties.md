## Starter Properties

### Define Properties Bean Class in Starter Module

* class annotated with `@ConfigurationProperties`
    * identifies class a being a configuration property class
    * `hello` attribute identifies the property prefix 
* class contains attributes that represent properties    
    * `greeting` identifies the property name
        * fully qualified property name `hello.greeting`
    * class can define a default
    * class can supply documentation


    import org.springframework.boot.context.properties.ConfigurationProperties;
    
    @ConfigurationProperties("hello")
    public class HelloProperties {
        /**
         * Set this value to what you want the Hello service to say when greeting.
         */
        private String greeting="Starter properties says Aloha";
    
        public String getGreeting() {
            return greeting;
        }
    
        public void setGreeting(String greeting) {
            this.greeting = greeting;
        }
    }

### Register Properties Bean Class in Starter Class
* `@EnableConfigurationProperties` used to identify the Properties Bean for scanning
* `@Bean` factory method accepts injected Properties Bean instance


    import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
    import org.springframework.boot.context.properties.EnableConfigurationProperties;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    
    import Hello;
    import StdOutHello;
    
    @Configuration
    @EnableConfigurationProperties(HelloProperties.class)
    public class HelloPropertiesStarter {
    
        @Bean
        @ConditionalOnMissingBean
        public Hello hello(HelloProperties config) {
            return new StdOutHello(config.getGreeting());
        }
    }


### Application Supplies Properties

* Option: `application.properties`


    #application.properties
    hello.greeting=application.properties says Wassup 
    
* Option: command line


    $ java -jar target/springboot-starterproperties-example-1.0-SNAPSHOT.jar \
        --hello.greeting="Command line property says Hi World"
    ...
    args=[--hello.greeting=Command line property says Hi World]
    Command line property says Hi World World

    
    
