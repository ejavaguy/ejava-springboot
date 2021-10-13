package info.ejava_student.assignment3.security.race;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication(scanBasePackageClasses = {
        SecureRaceApp.class, //scan here
        //RacerRegistrationConfiguration.class //scan the API solution
})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecureRaceApp {
    public static void main(String...args) {
        SpringApplication.run(SecureRaceApp.class, args);
    }
}