package info.ejava.examples.svc.aop.items.controllers;

import info.ejava.examples.svc.aop.items.dto.AddressDTO;
import info.ejava.examples.svc.aop.items.dto.ContactDTO;
import info.ejava.examples.svc.aop.items.services.ContactsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("api/contacts")
@RequiredArgsConstructor
public class ContactsController {
    private final ContactsService contactsService;

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ContactDTO> createContact(
            @RequestBody ContactDTO contact) {

        ContactDTO createdContact = contactsService.createContact(contact);

        URI url = ServletUriComponentsBuilder.fromCurrentRequestUri().path(""+createdContact.getContectId()).build().toUri();
        ResponseEntity<ContactDTO> response = ResponseEntity.created(url)
                .body(createdContact);
        return response;
    }

    @PutMapping(path="{contactId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ContactDTO> updateContact(
            @PathVariable("contactId") int id,
            @RequestBody ContactDTO contact) {

        ContactDTO updatedContact = contactsService.updateContact(id, contact);

        ResponseEntity<ContactDTO> response = ResponseEntity.ok(updatedContact);
        return response;
    }

    @PostMapping(path="{contactId}/addresses",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ContactDTO> addAddress(
            @PathVariable(name="contactId") int id,
            @RequestBody AddressDTO address) {

        ContactDTO updatedContact = contactsService.addAddress(id, address);

        ResponseEntity<ContactDTO> response = ResponseEntity.ok(updatedContact);
        return response;
    }

    @GetMapping(path="{contactId}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ContactDTO> getContact(
            @PathVariable("contactId") int id) {
        ContactDTO contact = contactsService.getContact(id);

        ResponseEntity<ContactDTO> response = ResponseEntity.ok(contact);
        return response;
    }

    @DeleteMapping(path="{contactId}")
    public ResponseEntity<Void> deleteContact(
            @PathVariable("contactId") int id) {
        contactsService.deleteContact(id);

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteContacts(@PathVariable("contactId") int id) {
        contactsService.deleteContacts();

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        return response;
    }
}
