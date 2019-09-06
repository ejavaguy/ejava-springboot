package info.ejava.examples.app.config.configproperties.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This class provides a example of ConfigurationProperties class that uses
 * a default constructor and Java bean setter()/getter() methods.
 */
@ConfigurationProperties("app.config.car")
public class CarProperties {
    /**
     * Name of car with no set maximum size
     */
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CarProperties{name='" + name + "\'}";
    }
}
