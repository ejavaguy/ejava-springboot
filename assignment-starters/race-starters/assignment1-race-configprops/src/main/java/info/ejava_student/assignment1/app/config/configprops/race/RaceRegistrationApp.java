package info.ejava_student.assignment1.app.config.configprops.race;

import info.ejava_student.assignment1.app.config.configprops.race.config.EventProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class RaceRegistrationApp {
    public static void main(String[] args) {
        SpringApplication.run(RaceRegistrationApp.class, args);
    }

    @Configuration
    public class RaceConfiguration {
        public List<EventProperties> swims() {
            return new ArrayList<>();
        }
        public List<EventProperties> runs() {
            return new ArrayList<>();
        }
    }

    //@Component
    @RequiredArgsConstructor
    public class PropertyPrinter implements CommandLineRunner {
        private final List<EventProperties> swims;
        private final List<EventProperties> runs;

        @Override
        public void run(String... args) throws Exception {
            System.out.println("swims:" + format(swims));
            System.out.println("runs:" + format(runs));
        }

        private String format(List<EventProperties> events) {
            return String.format("%s", events.stream()
                    .map(r->"*" + r.toString())
                    .collect(Collectors.joining(System.lineSeparator(), System.lineSeparator(), "")));
        }
    }
}
