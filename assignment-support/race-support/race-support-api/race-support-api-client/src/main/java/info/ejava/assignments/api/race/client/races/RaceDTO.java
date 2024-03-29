package info.ejava.assignments.api.race.client.races;

import com.fasterxml.jackson.annotation.JsonIgnore;
import info.ejava.assignments.api.race.client.StreetAddressDTO;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@With
@AllArgsConstructor
@NoArgsConstructor
public class RaceDTO {
    private String id;
    private String name;
    private double eventLength;
    LocalDate eventDate;
    StreetAddressDTO location;
    @JsonIgnore
    private String ownername;
}
