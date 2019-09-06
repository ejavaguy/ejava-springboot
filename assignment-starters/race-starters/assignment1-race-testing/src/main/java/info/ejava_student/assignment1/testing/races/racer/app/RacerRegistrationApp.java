package info.ejava_student.assignment1.testing.races.racer.app;

import info.ejava_student.assignment1.testing.races.racer.RacerRegistrationConfig;
import info.ejava_student.assignment1.testing.races.racer.dto.RacerDTO;
import info.ejava_student.assignment1.testing.races.racer.svc.InvalidInputException;
import info.ejava_student.assignment1.testing.races.racer.svc.RacerRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@SpringBootApplication(scanBasePackageClasses = {RacerRegistrationConfig.class})
public class RacerRegistrationApp {
    public static void main(String...args) {
        SpringApplication.run(RacerRegistrationApp.class, args);
    }

    @Component
    @RequiredArgsConstructor
    static class Init implements CommandLineRunner {
        private final RacerRegistrationService racerRegistrationService;
        @Override
        public void run(String... args) throws Exception {
            RacerDTO validRacer = RacerDTO.builder()
                    .firstName("michael")
                    .lastName("phelps")
                    .dob(LocalDate.of(1985,6, 30))
                    .build();
            racerRegistrationService.registerRacer(validRacer);

            RacerDTO invalidRacer = RacerDTO.builder()
                    .firstName("future")
                    .lastName("phelps")
                    .dob(LocalDate.now())
                    .build();
            try {
                racerRegistrationService.registerRacer(invalidRacer);
            } catch (InvalidInputException expected) {
            }
        }
    }
}
