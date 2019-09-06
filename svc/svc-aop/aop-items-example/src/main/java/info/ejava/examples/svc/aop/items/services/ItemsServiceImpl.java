package info.ejava.examples.svc.aop.items.services;

import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava.examples.svc.aop.items.dto.ItemDTO;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemsServiceImpl<T extends ItemDTO> implements ItemsService<T> {
    private AtomicInteger nextId = new AtomicInteger(1);
    private ConcurrentMap<Integer, T> items = new ConcurrentHashMap();

    @Override
    public T createItem(T item) {
        item.setId(nextId.getAndAdd(1));
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public T updateItem(int id, T item) {
        if (!items.containsKey(id)) {
           throw new ClientErrorException.NotFoundException("item[%d] not found", id);
        }
        items.put(id, item);
        item.setId(id);
        return item;
    }

    @Override
    public T getItem(int id) {
        T item = items.get(id);
        if (item==null) {
            throw new ClientErrorException.NotFoundException("item[%d] not found", id);
        }
        return item;
    }

    @Override
    public void deleteItem(int id) {
        items.remove(id);
    }

    @Override
    public void deleteItems() {
        items.clear();
    }

}
