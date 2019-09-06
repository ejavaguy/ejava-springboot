package info.ejava_student.assignment1.beanfactory.race.svc;

import info.ejava_student.assignment1.beanfactory.race.dto.RaceDTO;

import java.util.List;

public interface RaceRegistration {
    List<RaceDTO> getRaces(int pageSize, int pageNumber);
}
