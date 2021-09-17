package info.ejava.assignments.api.race.client;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreetAddressDTO {
    private String city;
    private String state;
}
