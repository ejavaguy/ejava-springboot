package info.ejava.examples.svc.aop.items.services;

import info.ejava.examples.svc.aop.items.dto.ItemDTO;

public interface ItemsService<T extends ItemDTO> {
    T createItem(T item);
    T updateItem(int id, T item);

    T getItem(int id);
    void deleteItem(int id);
    void deleteItems();
}
