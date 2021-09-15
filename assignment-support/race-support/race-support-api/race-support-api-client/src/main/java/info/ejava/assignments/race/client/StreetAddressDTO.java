package info.ejava.assignments.race.client;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreetAddressDTO {
    private String city;
    private String state;
}
