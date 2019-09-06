package info.ejava.examples.svc.docker.votes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteCountDTO {
    private String choice;
    private int votes;
}
