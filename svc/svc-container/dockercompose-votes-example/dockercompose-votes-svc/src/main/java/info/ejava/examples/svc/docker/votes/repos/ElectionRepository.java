package info.ejava.examples.svc.docker.votes.repos;

import info.ejava.examples.svc.docker.votes.bo.VoteBO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ElectionRepository extends JpaRepository<VoteBO, String> {
    @Query("select choice, count(id), max(date) from VoteBO group by choice order by count(id) DESC")
    public List<Object[]> countVotes();
}
