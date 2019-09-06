package info.ejava.examples.svc.docker.votes.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="VOTE")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteBO {
    @Id
    @Column(length = 50)
    private String id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Column(length = 40)
    private String source;
    @Column(length = 40)
    private String choice;
}
