package info.ejava.examples.svc.aop.items.controllers;

import info.ejava.examples.svc.aop.items.dto.ChairDTO;
import info.ejava.examples.svc.aop.items.dto.ItemDTO;
import info.ejava.examples.svc.aop.items.services.ChairsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/chairs")
@RequiredArgsConstructor
public class ChairsController {
    private final ChairsServiceImpl chairsService;

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ItemDTO> createItem(
            @RequestBody ChairDTO item) {

        ChairDTO createdItem = chairsService.createItem(item);

        URI url = ServletUriComponentsBuilder.fromCurrentRequestUri().path(""+createdItem.getId()).build().toUri();
        ResponseEntity<ItemDTO> response = ResponseEntity.created(url)
                .body(createdItem);
        return response;
    }

    @PutMapping(path="{itemId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ItemDTO> updateItem(
            @PathVariable("itemId") int id,
            @RequestBody ChairDTO item) {

        ChairDTO updatedItem = chairsService.updateItem(id, item);

        ResponseEntity<ItemDTO> response = ResponseEntity.ok(updatedItem);
        return response;
    }

    @GetMapping(path="{itemId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ItemDTO> getItem(
            @PathVariable("itemId") int id) {
        ChairDTO item = chairsService.getItem(id);

        ResponseEntity<ItemDTO> response = ResponseEntity.ok(item);
        return response;
    }

    @DeleteMapping(path="{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable("itemId") int id) {
        chairsService.deleteItem(id);

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteItems() {
        chairsService.deleteItems();

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }
}
