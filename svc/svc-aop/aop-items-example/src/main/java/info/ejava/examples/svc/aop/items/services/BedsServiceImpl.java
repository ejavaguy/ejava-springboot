package info.ejava.examples.svc.aop.items.services;

import info.ejava.examples.svc.aop.items.dto.BedDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
public class BedsServiceImpl extends ItemsServiceImpl<BedDTO> {
    @Override
    @Order(100)
    public BedDTO createItem(BedDTO item) {
        return super.createItem(item);
    }

    @Override
    public BedDTO updateItem(int id, BedDTO item) {
        return super.updateItem(id, item);
    }

    @Override
    public BedDTO getItem(int id) {
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
