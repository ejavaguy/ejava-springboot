package info.ejava_student.assignment1.testing.races.racer.svc;

import info.ejava.assignments.testing.race.racers.dto.RacerDTO;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@TestConfiguration
@ConfigurationPropertiesScan //needed when not using @SpringBootApplication
public class RacerTestConfiguration {
    @Bean
    public RacerDTO validRacer() {
        return RacerDTO.builder()
                .firstName("manny")
                .lastName("pep")
                .dob(LocalDate.ofYearDay(1923,1))
                .build();
    }
}
