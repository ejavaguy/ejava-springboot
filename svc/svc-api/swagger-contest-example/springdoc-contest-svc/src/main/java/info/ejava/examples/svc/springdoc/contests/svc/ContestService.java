package info.ejava.examples.svc.springdoc.contests.svc;

import info.ejava.examples.svc.springdoc.contests.dto.ContestDTO;
import info.ejava.examples.svc.springdoc.contests.dto.ContestListDTO;

public interface ContestService {
    ContestDTO createContest(ContestDTO newContest);
    ContestDTO getContest(int id);
    boolean doesContestExist(int id);
    void updateContest(int id, ContestDTO contestUpdate);
    void deleteContest(int id);

    ContestListDTO getContests(int offset, int limit);
    void deleteAllContests();
}
