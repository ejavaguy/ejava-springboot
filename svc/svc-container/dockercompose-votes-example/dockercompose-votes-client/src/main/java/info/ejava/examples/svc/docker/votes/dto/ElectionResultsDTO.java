package info.ejava.examples.svc.docker.votes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class ElectionResultsDTO {
    private Instant date;
    private List<VoteCountDTO> results = new ArrayList<>();

    public VoteCountDTO getChoice(String choice) {
        if (results==null) { return null; }
        return results.stream().filter(r->r.getChoice().equals(choice)).findFirst().orElse(null);
    }
}
