package info.ejava.examples.svc.docker.votes.repos;

import info.ejava.examples.svc.docker.votes.dto.VoteDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VoterRepository extends MongoRepository<VoteDTO, String> {
}
