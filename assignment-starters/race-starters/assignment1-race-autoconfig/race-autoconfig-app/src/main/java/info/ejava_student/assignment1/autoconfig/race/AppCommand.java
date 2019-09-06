package info.ejava_student.assignment1.autoconfig.race;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AppCommand implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        List<?> races = null;
        String msg = String.format("Race Registration has started, races:%s", races);
        System.out.println(msg);
    }
}
