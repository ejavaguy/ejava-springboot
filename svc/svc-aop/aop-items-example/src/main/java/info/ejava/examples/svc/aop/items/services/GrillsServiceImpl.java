package info.ejava.examples.svc.aop.items.services;

import info.ejava.examples.svc.aop.items.dto.GrillDTO;
import org.springframework.stereotype.Service;

@Service
public class GrillsServiceImpl extends ItemsServiceImpl<GrillDTO> {

    @Override
    public GrillDTO createItem(GrillDTO item) {
        return super.createItem(item);
    }

    @Override
    public GrillDTO updateItem(int id, GrillDTO item) {
        return super.updateItem(id, item);
    }

    @Override
    public GrillDTO getItem(int id) {
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
