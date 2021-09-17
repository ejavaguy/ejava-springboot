package info.ejava.assignments.testing.race.racers.svc;

import info.ejava.assignments.testing.race.racers.dto.RacerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
public class RacerRegistrationServiceImpl implements RacerRegistrationService {
    private final RacerValidator validator;
    private final int minAge;
    private static final AtomicInteger IDS = new AtomicInteger();

    public RacerRegistrationServiceImpl(RacerRegistrationProperties props, RacerValidator validator) {
        this.validator = validator;
        this.minAge = props.getMinAge();
        log.debug("initialized with minAge={}", minAge);
    }

    @Override
    public RacerDTO registerRacer(RacerDTO racer) throws InvalidInputException {
        validRegistration(racer);
        //...
        RacerDTO registeredRacer = racer.withId(Integer.valueOf(IDS.incrementAndGet()).toString());
        //...
        log.info("racer registered: {}", racer);
        return registeredRacer;
    }

    void validRegistration(RacerDTO racer) throws InvalidInputException {
        List<String> errorMsgs = validator.validateNewRacer(racer, minAge);
        if (!errorMsgs.isEmpty()) {
            log.info("invalid racer: {}, {}", racer, errorMsgs);
            throw new InvalidInputException("invalid racer: %s", errorMsgs);
        }
    }
}
