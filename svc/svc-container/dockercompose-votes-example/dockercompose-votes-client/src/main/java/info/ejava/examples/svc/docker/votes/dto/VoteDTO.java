package info.ejava.examples.svc.docker.votes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document("votes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteDTO {
    @Id
    private String id;
    private Instant date;
    private String source;
    private String choice;
}
