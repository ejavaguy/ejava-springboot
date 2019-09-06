package info.ejava.examples.db.validation.contacts.dto;

import lombok.*;

import javax.validation.Payload;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@PersonHasName
public class PersonPocDTO {
    @Null(groups = PocValidationGroups.Create.class,
        message = "cannot be specified for create")
    private String id;
    private String firstName;
    private String lastName;
    @Past(groups = Default.class)
    private LocalDate dob;
    @Size(min=1, message = "must have at least one contact point")
    private List<@NotNull @Valid ContactPointDTO> contactPoints;
}
