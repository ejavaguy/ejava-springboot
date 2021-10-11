package info.ejava.assignments.security.race.racers;

import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.racers.RacerListDTO;
import info.ejava.assignments.api.race.racers.RacersService;
import info.ejava.assignments.security.race.security.AuthorizationHelper;
import info.ejava.examples.common.exceptions.ClientErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * This layered form of RacersService is demonstrating how annotations
 * can be applied to methods to enforce some access. All work is delegated
 * to the secure form of the service that assumes these constraints are
 * in place and performs lower-level security work.
 *
 * Students integrating Racers into their application can use this class
 * as an example, but must use the SecureRacersService instead and
 * mimic the constraints of this class using path-based authorization constraints.
 * This class is only being used for a local unit integration test.
 */
@RequiredArgsConstructor
@Slf4j
public class AnnotatedSecureRacersServiceImpl implements RacersService {
    private final SecureRacersServiceImpl serviceImpl;

    @Override
    @PreAuthorize("isAuthenticated()")
    public RacerDTO createRacer(RacerDTO newRacer) {
        return serviceImpl.createRacer(newRacer);
    }

    @Override
    public RacerDTO getRacer(String id) {
        return serviceImpl.getRacer(id);
    }

    @Override
    public Optional<RacerDTO> getRacerByUsername(String username) {
        return serviceImpl.getRacerByUsername(username);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public RacerDTO updateRacer(String id, RacerDTO racerUpdate) {
        return serviceImpl.updateRacer(id, racerUpdate);
    }

    @Override
    public RacerListDTO getRacers(Integer pageSize, Integer pageNumber) {
        return serviceImpl.getRacers(pageSize, pageNumber);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void deleteRacer(String id) {
        serviceImpl.deleteRacer(id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllRacers() {
        serviceImpl.deleteAllRacers();
    }
}
