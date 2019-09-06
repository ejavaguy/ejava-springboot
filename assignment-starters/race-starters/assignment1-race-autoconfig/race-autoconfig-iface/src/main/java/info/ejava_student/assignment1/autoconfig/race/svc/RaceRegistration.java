package info.ejava_student.assignment1.autoconfig.race.svc;

import info.ejava_student.assignment1.autoconfig.race.dto.RaceDTO;

import java.util.List;

public interface RaceRegistration {
    List<RaceDTO> getRaces(int pageSize, int pageNumber);
}
