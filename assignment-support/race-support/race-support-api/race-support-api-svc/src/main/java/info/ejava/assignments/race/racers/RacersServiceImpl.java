package info.ejava.assignments.race.racers;

import info.ejava.assignments.race.client.racers.RacerDTO;
import info.ejava.assignments.race.client.racers.RacerListDTO;
import info.ejava.examples.common.exceptions.ClientErrorException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RacersServiceImpl implements RacersService {
    private final RacersRepository racersRepository;

    @Override
    public RacerDTO createRacer(RacerDTO newRace) {
        return racersRepository.save(newRace);
    }

    @Override
    public RacerDTO getRacer(String id) {
        return racersRepository.findById(id)
                .orElseThrow(()->new ClientErrorException.NotFoundException("Racer[%s] not found", id));
    }

    @Override
    public RacerDTO updateRacer(String id, RacerDTO updateRace) {
        if (updateRace==null) {
            throw new ClientErrorException.NotFoundException("Racer cannot be null");
        }
        return racersRepository.save(updateRace);
    }

    @Override
    public RacerListDTO getRacers(Integer pageSize, Integer pageNumber) {
        int size = pageSize==null || pageSize < 0 ? Integer.MAX_VALUE : pageSize;
        int num = pageNumber==null || pageNumber < 0 ? 0 : pageNumber;
        return new RacerListDTO(racersRepository.findAll(size, num));
    }

    @Override
    public void deleteRacer(String id) {
        RacerDTO race = racersRepository.findById(id)
                .orElse(new RacerDTO());

        racersRepository.delete(race);
    }

    @Override
    public void deleteAllRacers() {
        racersRepository.deleteAll();
    }
}
