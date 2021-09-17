package info.ejava.assignments.testing.race.racers.svc;

import info.ejava.assignments.testing.race.racers.dto.RacerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class RacerValidatorImpl implements RacerValidator {

    @Override
    public List<String> validateNewRacer(RacerDTO racer, int minAge) {
        List<String> errorMsgs = new ArrayList<>();
        log.trace("validating {}, minAge={}", racer, minAge);

        if (racer == null) {
            errorMsgs.add("racer: cannot be null");
        } else {
            if (racer.getId() != null) {
                errorMsgs.add("id must be null");
            }
            if (ObjectUtils.isEmpty(racer.getFirstName())) {
                errorMsgs.add("racer.firstName: cannot be blank");
            }
            if (ObjectUtils.isEmpty(racer.getLastName())) {
                errorMsgs.add("racer.lastName: cannot be blank");
            }
            if (racer.getDob() == null) {
                errorMsgs.add("racer.dob: cannot be null");
            } else {
                LocalDate minDob = LocalDate.now().minusYears(minAge);
                if (racer.getDob().isAfter(minDob)) {
                    errorMsgs.add(String.format("racer.dob: must be greater than %d years", minAge));
                }
            }
        }
        log.trace("racer {}, valid={}, errors={}", racer, errorMsgs.isEmpty(), errorMsgs);
        return errorMsgs;
    }
}
