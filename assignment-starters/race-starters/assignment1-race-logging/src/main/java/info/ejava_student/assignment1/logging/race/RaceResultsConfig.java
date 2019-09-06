package info.ejava_student.assignment1.logging.race;

import info.ejava_student.assignment1.logging.race.app.AppCommand;
import info.ejava_student.assignment1.logging.race.repo.RaceResultsRepository;
import info.ejava_student.assignment1.logging.race.svc.RaceResultsHelper;
import info.ejava_student.assignment1.logging.race.svc.RaceResultsService;
import org.springframework.context.annotation.Configuration;

public class RaceResultsConfig {
    public RaceResultsRepository repo() {
        return null;
    }
    public RaceResultsHelper helper() {
        return null;
    }
    public RaceResultsService service(RaceResultsRepository repo, RaceResultsHelper helper) {
        return null;
    }
    public AppCommand appCommand(RaceResultsService service) {
        return null;
    }
}
