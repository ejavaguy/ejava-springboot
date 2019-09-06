package info.ejava.examples.svc.docker.votes.services;

import info.ejava.examples.svc.docker.votes.dto.VoteDTO;
import info.ejava.examples.svc.docker.votes.repos.VoterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class VoterServiceImpl implements VoterService {
    private final VoterRepository voterRepository;

    @Override
    public VoteDTO castVote(VoteDTO newVote) {
        newVote.setId(null);
        newVote.setDate(Instant.now());
        return voterRepository.insert(newVote);
    }

    @Override
    public long getTotalVotes() {
        return voterRepository.count();
    }

    @Override
    public Page<VoteDTO> getVotes(int offset, int limit) {
        Pageable pageable = toPageable(offset, limit, Sort.by("date"));
        Page<VoteDTO> page = voterRepository.findAll(pageable);
        return page;
    }

    protected Pageable toPageable(int offset, int limit, Sort sort) {
        if (limit<=0) { limit=Integer.MAX_VALUE; }
        if (offset<0) { offset=0; }
        int pageNo = offset/limit;
        return sort==null ?
            PageRequest.of(pageNo, limit) :
            PageRequest.of(pageNo, limit, sort);
    }
}
