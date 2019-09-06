package info.ejava_student.assignment1.app.config.configprops.race.config;

import lombok.Value;

@Value
public class LocationProperties {
    //@NotBlank
    private final String city;
    private final String state;
}
