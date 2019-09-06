package info.ejava_student.assignment1.beanfactory.race;

import org.springframework.boot.CommandLineRunner;

import java.util.ArrayList;
import java.util.List;

public class AppCommand implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        List<?> races = new ArrayList<>();
        String msg = String.format("Race Registration has started, races:%s", races);
        System.out.println(msg);
    }
}
