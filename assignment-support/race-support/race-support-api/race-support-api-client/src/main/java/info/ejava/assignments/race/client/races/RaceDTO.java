package info.ejava.assignments.race.client.races;

import info.ejava.assignments.race.client.StreetAddressDTO;
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
}
