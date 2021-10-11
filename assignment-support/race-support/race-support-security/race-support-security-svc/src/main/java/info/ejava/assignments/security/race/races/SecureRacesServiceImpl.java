package info.ejava.assignments.security.race.races;

import info.ejava.assignments.api.race.client.races.RaceDTO;
import info.ejava.assignments.api.race.client.races.RaceListDTO;
import info.ejava.assignments.api.race.races.RacesService;
import info.ejava.assignments.security.race.security.AuthorizationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;

@RequiredArgsConstructor
@Slf4j
public class SecureRacesServiceImpl implements RacesService {
    private final RacesService serviceImpl;
    private final AuthorizationHelper authzHelper;

    @Override
    public RaceDTO createRace(RaceDTO newRace) {
        String username = authzHelper.getUsername();;
        log.debug("{} called createRace with {}", username, newRace);

        newRace.setOwnername(authzHelper.getUsername());
        return serviceImpl.createRace(newRace);
    }

    @Override
    public RaceDTO getRace(String id) {
        return serviceImpl.getRace(id);
    }

    @Override
    public RaceDTO updateRace(String id, RaceDTO updateRace) {
        authzHelper.isOwnerOrAuthority(()->serviceImpl.getRace(id).getOwnername(), null);
        return serviceImpl.updateRace(id, updateRace);
    }

    @Override
    public RaceDTO cancelRace(String id) {
        authzHelper.isOwnerOrAuthority(()->serviceImpl.getRace(id).getOwnername(), null);
        return serviceImpl.cancelRace(id);
    }

    @Override
    public RaceListDTO getRaces(Integer pageSize, Integer pageNumber) {
        return serviceImpl.getRaces(pageSize, pageNumber);
    }

    @Override
    public void deleteRace(String id) {
        authzHelper.isOwnerOrAuthority(()->serviceImpl.getRace(id).getOwnername(), "ROLE_MGR");
        serviceImpl.deleteRace(id);
    }

    @Override
    public void deleteAllRaces() {
        serviceImpl.deleteAllRaces();
    }
}
