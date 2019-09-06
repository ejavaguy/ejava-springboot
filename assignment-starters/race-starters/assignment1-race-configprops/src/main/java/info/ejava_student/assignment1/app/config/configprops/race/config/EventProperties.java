package info.ejava_student.assignment1.app.config.configprops.race.config;

import lombok.Value;
import org.springframework.boot.context.properties.ConstructorBinding;

@Value
@ConstructorBinding
public class EventProperties {
    private String name;
    private LocationProperties address;
    private String distance;
}
