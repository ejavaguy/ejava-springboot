package info.ejava.examples.svc.aop.items.controllers;

import info.ejava.examples.svc.aop.items.dto.ItemDTO;
import info.ejava.examples.svc.aop.items.services.ItemsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public class ItemsController<T extends ItemDTO> {
    private final ItemsService<T> itemsService;

    public ItemsController(ItemsService<T> service) {
        this.itemsService = service;
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ItemDTO> createItem(
            @RequestBody T item) {

        T createdItem = itemsService.createItem(item);

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
            @RequestBody T item) {

        T updatedItem = itemsService.updateItem(id, item);

        ResponseEntity<ItemDTO> response = ResponseEntity.ok(updatedItem);
        return response;
    }

    @GetMapping(path="{itemId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ItemDTO> getItem(
            @PathVariable("itemId") int id) {
        T item = itemsService.getItem(id);

        ResponseEntity<ItemDTO> response = ResponseEntity.ok(item);
        return response;
    }

    @DeleteMapping(path="{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable("itemId") int id) {
        itemsService.deleteItem(id);

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteItems() {
        itemsService.deleteItems();

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }
}
