package info.ejava_student.assignment1.testing.races.racer.dto;

import lombok.*;

import java.time.LocalDate;
@Value
@With
@Builder
@AllArgsConstructor
public class RacerDTO {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final LocalDate dob;
}
