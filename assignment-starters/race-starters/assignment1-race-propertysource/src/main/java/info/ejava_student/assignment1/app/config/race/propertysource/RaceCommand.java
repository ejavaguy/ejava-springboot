package info.ejava_student.assignment1.app.config.race.propertysource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RaceCommand implements CommandLineRunner {
    @Value("${spring.config.name:(default value)}") String configName;
    @Value("${spring.config.location:(default value)}") String configLocations;
    @Value("${spring.profiles.active:(default value)}") String profilesActive;

    @Value("${race.priority.source:not assigned}") String prioritySource;
    @Value("${race.db.url:not assigned}") String dbUrl;

    @Override
    public void run(String... args) throws Exception {
        String settings = String.format("\nconfigName=%s\nconfigLocation=%s\nprofilesActive=%s\nprioritySource=%s",
                configName,
                configLocations,
                profilesActive,
                prioritySource);
        System.out.println(settings);
        String msg = String.format("Race Registration has started\ndbUrl=%s",
                dbUrl);
        System.out.println(msg);
    }
}
