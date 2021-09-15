package info.ejava.assignments.race.races;

import info.ejava.assignments.race.client.races.RaceDTO;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RacesRespositoryImpl implements RacesRepository {
    private final AtomicInteger ID = new AtomicInteger(0);
    private final Map<String, RaceDTO> races = new ConcurrentHashMap<>();

    @Override
    public RaceDTO save(RaceDTO race) {
        if (race!=null) {
            String id = race.getId()!=null ?
                    race.getId() :
                    Integer.valueOf(ID.incrementAndGet()).toString();
            race.setId(id);
            races.put(race.getId(), race);
        }
        return race;
    }

    @Override
    public boolean existsById(String id) {
        return id==null ? false : races.containsKey(id);
    }

    @Override
    public Optional<RaceDTO> findById(String id) {
        RaceDTO race = races.get(id);
        return race!=null ? Optional.of(race) : Optional.empty();
    }

    @Override
    public List<RaceDTO> findAll(int pageSize, int pageNumber) {
        int startAt = pageSize * pageNumber;
        int endBefore = startAt + pageSize < races.size() ? startAt + pageSize : races.size();
        return startAt < 0 || startAt >= races.size() ?
            Collections.emptyList() :
            new ArrayList<>(races.values()).subList(startAt, endBefore);
    }

    @Override
    public void delete(RaceDTO race) {
        if (race.getId()!=null) {
            races.remove(race.getId());
        }
    }

    @Override
    public void deleteAll() {
        races.clear();
    }
}
