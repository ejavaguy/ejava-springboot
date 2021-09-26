package info.ejava.assignments.api.race.races;

import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava.assignments.api.race.client.races.RaceDTO;
import info.ejava.assignments.api.race.client.races.RaceListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
public class RacesServiceImpl implements RacesService {
    private final RacesRepository racesRepository;

    @Override
    public RaceDTO createRace(RaceDTO newRace) {
        RaceDTO createdRace = racesRepository.save(newRace);
        log.debug("created race: {}", createdRace);
        return createdRace;
    }

    @Override
    public RaceDTO getRace(String id) {
        return racesRepository.findById(id)
                .orElseThrow(()->{
                    log.debug("getRace({}) not found", id);
                    return new ClientErrorException.NotFoundException("Race[%s] not found", id);
                });
    }

    @Override
    public RaceDTO updateRace(String id, RaceDTO updateRace) {
        if (null==updateRace) {
            throw new ClientErrorException.NotFoundException("Race cannot be null");
        }
        if (null!=id) {
            updateRace.setId(id);
        }

        log.debug("updating race id={}, {}", id, updateRace);
        return racesRepository.save(updateRace);
    }

    @Override
    public RaceDTO cancelRace(String id) {
        RaceDTO race = racesRepository.findById(id)
                .orElseThrow(()->{
                    log.debug("race[{}] not found", id);
                    return new ClientErrorException.NotFoundException("Race[%s] not found", id);
                });
        deleteRace(race.getId());
        return race;
    }

    @Override
    public RaceListDTO getRaces(Integer pageSize, Integer pageNumber) {
        //create value paging values
        int size = pageSize==null || pageSize < 0 ? Integer.MAX_VALUE : pageSize;
        int num = pageNumber==null || pageNumber < 0 ? 0 : pageNumber;
        log.debug("pageSize {}=>{}, pageNumber {}=>{}", pageSize, size, pageNumber, num);
        return new RaceListDTO(racesRepository.findAll(size, num));
    }

    @Override
    public void deleteRace(String id) {
        RaceDTO race = racesRepository.findById(id)
                .orElse(new RaceDTO());
        log.debug("deleting race[{}] found={}", id, null!=race.getId());
        racesRepository.delete(race);
    }

    @Override
    public void deleteAllRaces() {
        log.warn("deleting all races!!!");
        racesRepository.deleteAll();
    }
}
