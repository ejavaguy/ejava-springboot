package info.ejava.assignments.race.racers;

import info.ejava.assignments.race.client.racers.RacerDTO;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RacersRepositoryImpl implements RacersRepository {
    private final AtomicInteger ID = new AtomicInteger(0);
    private final Map<String, RacerDTO> racers = new ConcurrentHashMap<>();

    @Override
    public RacerDTO save(RacerDTO race) {
        if (race!=null) {
            String id = race.getId()!=null ?
                    race.getId() :
                    Integer.valueOf(ID.incrementAndGet()).toString();
            race.setId(id);
            racers.put(race.getId(), race);
        }
        return race;
    }

    @Override
    public boolean existsById(String id) {
        return id==null ? false : racers.containsKey(id);
    }

    @Override
    public Optional<RacerDTO> findById(String id) {
        RacerDTO race = racers.get(id);
        return race!=null ? Optional.of(race) : Optional.empty();
    }

    @Override
    public List<RacerDTO> findAll(int pageSize, int pageNumber) {
        int startAt = pageSize * pageNumber;
        int endBefore = startAt + pageSize < racers.size() ? startAt + pageSize : racers.size();
        return startAt < 0 || startAt >= racers.size() ?
                Collections.emptyList() :
                new ArrayList<>(racers.values()).subList(startAt, endBefore);
    }

    @Override
    public void delete(RacerDTO race) {
        if (race.getId()!=null) {
            racers.remove(race.getId());
        }
    }

    @Override
    public void deleteAll() {
        racers.clear();
    }
}
