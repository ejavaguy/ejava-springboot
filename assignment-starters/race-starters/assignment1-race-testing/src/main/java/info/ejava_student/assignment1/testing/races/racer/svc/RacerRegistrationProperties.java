package info.ejava_student.assignment1.testing.races.racer.svc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@Validated
//@ConfigurationProperties("racer.registration")
public class RacerRegistrationProperties {
    @NotNull
    private Integer minAge;
}
