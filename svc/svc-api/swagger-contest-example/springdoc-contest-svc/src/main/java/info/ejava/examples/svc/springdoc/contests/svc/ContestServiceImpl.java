package info.ejava.examples.svc.springdoc.contests.svc;

import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava.examples.common.exceptions.ClientErrorException.BadRequestException;
import info.ejava.examples.common.exceptions.ClientErrorException.NotFoundException;
import info.ejava.examples.svc.springdoc.contests.dto.ContestDTO;
import info.ejava.examples.svc.springdoc.contests.dto.ContestListDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class ContestServiceImpl implements ContestService {
    private final AtomicInteger nextId = new AtomicInteger(1);
    private final Map<Integer, ContestDTO> contests;

    public ContestServiceImpl(Map<Integer, ContestDTO> contestMap) {
        this.contests = contestMap;
    }

    @Override
    public ContestDTO createContest(ContestDTO contest) {
        validate(contest);
        contest.setId(nextId.getAndAdd(1));
        contests.put(contest.getId(), contest);
        return contest;
    }

    @Override
    public void updateContest(int id, ContestDTO contest) {
        validate(contest);
        if (contests.containsKey(id)) {
            contest.setId(id);
            contests.put(contest.getId(), contest);
        } else {
            throw new NotFoundException("contest[%d] not found", id);
        }
    }

    protected void validate(ContestDTO contest) {
        if (StringUtils.isEmpty(contest.getHomeTeam())) {
            throw new ClientErrorException.InvalidInputException("home team is required");
        }
        if (StringUtils.isEmpty(contest.getAwayTeam())) {
            throw new ClientErrorException.InvalidInputException("away team is required");
        }
    }

    @Override
    public void deleteContest(int id) {
        contests.remove(id);
    }

    @Override
    public void deleteAllContests() {
        contests.clear();
    }

    @Override
    public boolean doesContestExist(int id) {
        return contests.containsKey(id);
    }

    @Override
    public ContestDTO getContest(int id) {
        ContestDTO contest = contests.get(id);
        if (contest!=null) {
            return contest;
        } else {
            throw new NotFoundException("contest[%d] not found", id);
        }
    }

    @Override
    public ContestListDTO getContests(int offset, int limit) {
        validatePaging(offset, limit);

        List<ContestDTO> responses = new ArrayList<>(limit);
        Iterator<ContestDTO> itr = contests.values().iterator();
        for (int i=0; itr.hasNext() && (limit==0 || responses.size()<limit); i++) {
            ContestDTO q = itr.next();
            if (i>=offset) {
                responses.add(q);
            }
        }

        return ContestListDTO.builder()
                .offset(offset)
                .limit(limit)
                .total(contests.size())
                .contests(responses)
                .build();
    }

    public void validatePaging(int offset, int limit) {
        if (offset<0 || limit<0) {
            throw new BadRequestException("offset[%d] or limit[%d] must be >=0", offset, limit);
        }
    }
}
