package info.ejava.examples.svc.aop.items.services;

import info.ejava.examples.svc.aop.items.dto.MowerDTO;
import org.springframework.stereotype.Service;

@Service
public class MowersServiceImpl extends ItemsServiceImpl<MowerDTO> {
    @Override
    public MowerDTO createItem(MowerDTO item) {
        return super.createItem(item);
    }

    @Override
    public MowerDTO updateItem(int id, MowerDTO item) {
        return super.updateItem(id, item);
    }

    @Override
    public MowerDTO getItem(int id) {
        return super.getItem(id);
    }

    @Override
    public void deleteItem(int id) {
        super.deleteItem(id);
    }

    @Override
    public void deleteItems() {
        super.deleteItems();
    }
}
