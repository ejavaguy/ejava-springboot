package info.ejava.examples.svc.aop.items.controllers;

import info.ejava.examples.svc.aop.items.dto.BedDTO;
import info.ejava.examples.svc.aop.items.services.ItemsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/beds")
public class BedsController extends ItemsController<BedDTO>{
    BedsController(ItemsService<BedDTO> service) {
        super(service);
    }
}
