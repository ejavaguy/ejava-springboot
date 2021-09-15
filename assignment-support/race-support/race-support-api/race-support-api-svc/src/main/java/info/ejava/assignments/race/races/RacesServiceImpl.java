package info.ejava.assignments.race.races;

import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava.assignments.race.client.races.RaceDTO;
import info.ejava.assignments.race.client.races.RaceListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RacesServiceImpl implements RacesService {
    private final RacesRepository racesRepository;

    @Override
    public RaceDTO createRace(RaceDTO newRace) {
        return racesRepository.save(newRace);
    }

    @Override
    public RaceDTO getRace(String id) {
        return racesRepository.findById(id)
                .orElseThrow(()->new ClientErrorException.NotFoundException("Race[%s] not found", id));
    }

    @Override
    public RaceDTO updateRace(String id, RaceDTO updateRace) {
        if (updateRace==null) {
            throw new ClientErrorException.NotFoundException("Race cannot be null");
        }
        return racesRepository.save(updateRace);
    }

    @Override
    public RaceDTO cancelRace(String id) {
        RaceDTO race = racesRepository.findById(id)
                .orElseThrow(()->new ClientErrorException.NotFoundException("Race[%s] not found", id));
        deleteRace(race.getId());
        return race;
    }

    @Override
    public RaceListDTO getRaces(Integer pageSize, Integer pageNumber) {
        int size = pageSize==null || pageSize < 0 ? Integer.MAX_VALUE : pageSize;
        int num = pageNumber==null || pageNumber < 0 ? 0 : pageNumber;
        return new RaceListDTO(racesRepository.findAll(size, num));
    }

    @Override
    public void deleteRace(String id) {
        RaceDTO race = racesRepository.findById(id)
                .orElse(new RaceDTO());

        racesRepository.delete(race);
    }

    @Override
    public void deleteAllRaces() {
        racesRepository.deleteAll();
    }
}
