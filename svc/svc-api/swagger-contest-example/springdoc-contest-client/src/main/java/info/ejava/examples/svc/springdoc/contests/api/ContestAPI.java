package info.ejava.examples.svc.springdoc.contests.api;

import info.ejava.examples.svc.springdoc.contests.dto.ContestDTO;
import info.ejava.examples.svc.springdoc.contests.dto.ContestListDTO;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface ContestAPI {
    public static final String CONTESTS_PATH = "/api/contests";
    public static final String CONTEST_PATH = CONTESTS_PATH + "/{contestId}";

    public Mono<ResponseEntity<ContestListDTO>> getContests(Integer offset, Integer limit);

    public Mono<ResponseEntity<ContestDTO>> createContest(ContestDTO contest);

    public Mono<ResponseEntity<Void>> updateContest(int id, ContestDTO contest);

    public Mono<ResponseEntity<Void>> containsContest(int id);

    public Mono<ResponseEntity<ContestDTO>> getContest(int id);

    public Mono<ResponseEntity<Void>> deleteContest(int id);

    public Mono<ResponseEntity<Void>> deleteAllContests();
}
