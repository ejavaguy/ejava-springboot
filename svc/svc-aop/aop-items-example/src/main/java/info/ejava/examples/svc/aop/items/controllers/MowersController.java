package info.ejava.examples.svc.aop.items.controllers;

import info.ejava.examples.svc.aop.items.dto.MowerDTO;
import info.ejava.examples.svc.aop.items.services.ItemsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mowers")
public class MowersController extends ItemsController<MowerDTO> {
    MowersController(ItemsService<MowerDTO> service) {
        super(service);
    }
}
