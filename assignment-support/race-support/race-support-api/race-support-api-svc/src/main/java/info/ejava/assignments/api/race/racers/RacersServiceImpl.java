package info.ejava.assignments.api.race.racers;

import info.ejava.assignments.api.race.client.racers.RacerDTO;
import info.ejava.assignments.api.race.client.racers.RacerListDTO;
import info.ejava.examples.common.exceptions.ClientErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class RacersServiceImpl implements RacersService {
    private final RacersRepository racersRepository;

    @Override
    public RacerDTO createRacer(RacerDTO newRace) {
        log.debug("saving {}", newRace);
        return racersRepository.save(newRace);
    }

    @Override
    public RacerDTO getRacer(String id) {
        return racersRepository.findById(id)
                .orElseThrow(()->{
                    log.debug("getRacer({}) not found", id);
                    return new ClientErrorException.NotFoundException("Racer[%s] not found", id);
                });
    }

    @Override
    public Optional<RacerDTO> getRacerByUsername(String username) {
        return racersRepository.findByUsername(username);
    }

    @Override
    public RacerDTO updateRacer(String id, RacerDTO updateRace) {
        if (null==updateRace) {
            throw new ClientErrorException.NotFoundException("Racer cannot be null");
        }
        if (null!=id) {
            updateRace.setId(id);
        }

        log.debug("updataing racer id={}, {}", id, updateRace);
        return racersRepository.save(updateRace);
    }

    @Override
    public RacerListDTO getRacers(Integer pageSize, Integer pageNumber) {
        int size = pageSize==null || pageSize < 0 ? Integer.MAX_VALUE : pageSize;
        int num = pageNumber==null || pageNumber < 0 ? 0 : pageNumber;
        log.debug("pageSize {}=>{}, pageNumber {}=>{}", pageSize, size, pageNumber, num);
        return new RacerListDTO(racersRepository.findAll(size, num));
    }

    @Override
    public void deleteRacer(String id) {
        RacerDTO racer = racersRepository.findById(id)
                .orElse(new RacerDTO());
        log.debug("deleting racer[{}] found={}", id, null!=racer.getId());
        racersRepository.delete(racer);
    }

    @Override
    public void deleteAllRacers() {
        log.warn("deleting all racers!!!");
        racersRepository.deleteAll();
    }
}
