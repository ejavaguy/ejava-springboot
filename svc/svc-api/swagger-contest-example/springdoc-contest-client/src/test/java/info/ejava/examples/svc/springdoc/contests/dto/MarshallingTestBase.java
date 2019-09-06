package info.ejava.examples.svc.springdoc.contests.dto;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

import static info.ejava.examples.svc.springdoc.contests.dto.ContestDTOFactory.oneUpId;
import static org.assertj.core.api.BDDAssertions.then;

@Slf4j
public abstract class MarshallingTestBase {
    protected static final Faker faker = new Faker();
    protected ContestDTOFactory contestDTOFactory = new ContestDTOFactory();

    public void init() {}
    
    protected abstract <T> String marshal(T object) throws Exception;
    protected abstract <T> T unmarshal(Class<T> type, String buffer) throws Exception;

    private <T> T marshal_and_demarshal(T obj, Class<T> type) throws Exception {
        String buffer = marshal(obj);
        T result = unmarshal(type, buffer);
        return result;
    }

    @Test
    public void contest_dto_marshals() throws Exception {
        //given - a contest
        ContestDTO contest = contestDTOFactory.make();

        //when - marshalled to a string and demarshalled back to an object
        ContestDTO result = marshal_and_demarshal(contest, ContestDTO.class);

        //then
        compare(result, contest);
    }

    @Test
    public void contestsList_dto_marshals() throws Exception {
        //given - some contests
        ContestListDTO contestsList = contestDTOFactory.listBuilder().make(3,3, oneUpId);

        //when
        ContestListDTO result = marshal_and_demarshal(contestsList, ContestListDTO.class);

        //then
        then(result.getCount()).isEqualTo(contestsList.getCount());
        Map<Integer, ContestDTO> contestMap = result.getContests().stream().collect(Collectors.toMap(ContestDTO::getId, q->q));
        for (ContestDTO expected: contestsList.getContests()) {
            ContestDTO actual = contestMap.remove(expected.getId());
            compare(actual, expected);
        }
    }

    private void compare(ContestDTO lhs, ContestDTO rhs) {
        then(lhs.getAwayScore()).isEqualTo(rhs.getAwayScore());
        then(lhs.getHomeScore()).isEqualTo(rhs.getHomeScore());
        then(lhs.getAwayTeam()).isEqualTo(rhs.getAwayTeam());
        then(lhs.getHomeTeam()).isEqualTo(rhs.getHomeTeam());
        then(lhs.getScheduledStart()).isEqualTo(rhs.getScheduledStart());
        then(lhs.getDuration()).isEqualTo(rhs.getDuration());
        then(lhs.getId()).isEqualTo(rhs.getId());
    }
}
