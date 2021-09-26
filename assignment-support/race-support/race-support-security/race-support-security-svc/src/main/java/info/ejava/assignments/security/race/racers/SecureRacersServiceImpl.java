package info.ejava.assignments.security.race.racers;

import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.racers.RacerListDTO;
import info.ejava.assignments.api.race.racers.RacersService;
import info.ejava.assignments.security.race.security.AuthorizationHelper;
import info.ejava.examples.common.exceptions.ClientErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

@RequiredArgsConstructor
public class SecureRacersServiceImpl implements RacersService {
    private final RacersService serviceImpl;
    private final AuthorizationHelper authzHelper;

    @Override
    @PreAuthorize("isAuthenticated()")
    public RacerDTO createRacer(RacerDTO newRacer) {
        Optional<RacerDTO> existingRacer = serviceImpl.getRacerByUsername(authzHelper.getUsername());
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
    @PreAuthorize("isAuthenticated()")
    public RacerDTO updateRacer(String id, RacerDTO racerUpdate) {
        authzHelper.isOwnerOrAuthority(()->serviceImpl.getRacer(id).getUsername(), null);
        return serviceImpl.updateRacer(id, racerUpdate);
    }

    @Override
    public RacerListDTO getRacers(Integer pageSize, Integer pageNumber) {
        return serviceImpl.getRacers(pageSize, pageNumber);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void deleteRacer(String id) {
        authzHelper.isOwnerOrAuthority(()->serviceImpl.getRacer(id).getUsername(), "ROLE_MGR");
        serviceImpl.deleteRacer(id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllRacers() {
        serviceImpl.deleteAllRacers();
    }
}
