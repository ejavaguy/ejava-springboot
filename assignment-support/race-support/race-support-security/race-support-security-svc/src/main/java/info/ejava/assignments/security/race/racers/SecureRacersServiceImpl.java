package info.ejava.assignments.security.race.racers;

import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.racers.RacerListDTO;
import info.ejava.assignments.api.race.racers.RacersService;
import info.ejava.assignments.security.race.security.AuthorizationHelper;
import info.ejava.examples.common.exceptions.ClientErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class SecureRacersServiceImpl implements RacersService {
    private final RacersService serviceImpl;
    private final AuthorizationHelper authzHelper;

    @Override
    //@PreAuthorize("isAuthenticated()")
    public RacerDTO createRacer(RacerDTO newRacer) {
        String username = authzHelper.getUsername();;
        log.debug("{} called createRacer with {}", username, newRacer);

        Optional<RacerDTO> existingRacer = serviceImpl.getRacerByUsername(username);
        if (existingRacer.isPresent()) {
            throw new ClientErrorException.InvalidInputException(
                    "racer already exists for %s", authzHelper.getUsername());
        }
        newRacer.setUsername(authzHelper.getUsername());
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
    //@PreAuthorize("isAuthenticated()")
    public RacerDTO updateRacer(String id, RacerDTO racerUpdate) {
        authzHelper.assertAuthorityOrOwner(null, ()->serviceImpl.getRacer(id).getUsername());
        return serviceImpl.updateRacer(id, racerUpdate);
    }

    @Override
    public RacerListDTO getRacers(Integer pageSize, Integer pageNumber) {
        return serviceImpl.getRacers(pageSize, pageNumber);
    }

    @Override
    //@PreAuthorize("isAuthenticated()")
    public void deleteRacer(String id) {
        authzHelper.assertAuthorityOrOwner("ROLE_MGR", ()->serviceImpl.getRacer(id).getUsername());
        serviceImpl.deleteRacer(id);
    }

    @Override
    //@PreAuthorize("hasRole('ADMIN')")
    public void deleteAllRacers() {
        serviceImpl.deleteAllRacers();
    }
}
