package info.ejava.examples.svc.aop.items.controllers;

import info.ejava.examples.svc.aop.items.dto.GrillDTO;
import info.ejava.examples.svc.aop.items.dto.ItemDTO;
import info.ejava.examples.svc.aop.items.services.ItemsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/grills")
public class GrillsController extends ItemsController<GrillDTO> {
    GrillsController(ItemsService<GrillDTO> service) {
        super(service);
    }

    @Override
    public ResponseEntity<ItemDTO> createItem(@RequestBody GrillDTO item) {
        return super.createItem(item);
    }

    @Override
    public ResponseEntity<ItemDTO> updateItem(
              @PathVariable("itemId") int id,
              @RequestBody GrillDTO item) {
        return super.updateItem(id, item);
    }
}
