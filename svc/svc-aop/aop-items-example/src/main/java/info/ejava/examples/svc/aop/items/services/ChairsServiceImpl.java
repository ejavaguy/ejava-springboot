package info.ejava.examples.svc.aop.items.services;

import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava.examples.svc.aop.items.dto.ChairDTO;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ChairsServiceImpl {
    private AtomicInteger nextId = new AtomicInteger(1);
    private ConcurrentMap<Integer, ChairDTO> chairs = new ConcurrentHashMap();

    public ChairDTO createItem(ChairDTO chair) {
        chair.setId(nextId.getAndAdd(1));
        chairs.put(chair.getId(), chair);
        return chair;
    }

    public ChairDTO updateItem(int id, ChairDTO chair) {
        if (!chairs.containsKey(id)) {
            throw new ClientErrorException.NotFoundException("chair[%d] not found", id);
        }
        chairs.put(id, chair);
        chair.setId(id);
        return chair;
    }

    public ChairDTO getItem(int id) {
        ChairDTO chair = chairs.get(id);
        if (chair==null) {
            throw new ClientErrorException.NotFoundException("chair[%d] not found", id);
        }
        return chair;
    }

    public void deleteItem(int id) {
        chairs.remove(id);
    }

    public void deleteItems() {
        chairs.clear();
    }
}
