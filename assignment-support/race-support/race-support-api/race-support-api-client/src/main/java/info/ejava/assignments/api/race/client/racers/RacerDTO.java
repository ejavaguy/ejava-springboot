package info.ejava.assignments.api.race.client.racers;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@With
@AllArgsConstructor
@NoArgsConstructor
public class RacerDTO {
    public static String[] GENDERS = {"M", "F"};
    private String id;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String gender;
}
