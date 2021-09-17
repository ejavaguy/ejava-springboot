package info.ejava.assignments.testing.race.racers.svc;

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
