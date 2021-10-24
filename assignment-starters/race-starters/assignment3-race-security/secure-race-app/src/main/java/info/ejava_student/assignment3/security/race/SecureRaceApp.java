package info.ejava_student.assignment3.security.race;

import info.ejava_student.assignment3.aop.race.config.AOPConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
        SecureRaceApp.class, //scan here
        AOPConfiguration.class //add AOP configuration
        //RacerRegistrationConfiguration.class //scan the API solution
})
public class SecureRaceApp {
    public static void main(String...args) {
        SpringApplication.run(SecureRaceApp.class, args);
    }
}