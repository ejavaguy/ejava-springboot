package info.ejava.examples.svc.aop.items.services;

import info.ejava.examples.svc.aop.items.dto.AddressDTO;
import info.ejava.examples.svc.aop.items.dto.ContactDTO;

public interface ContactsService {
    ContactDTO createContact(ContactDTO contact);
    ContactDTO updateContact(int id, ContactDTO contact);
    ContactDTO addAddress(int id, AddressDTO address);

    ContactDTO getContact(int id);
    void deleteContact(int id);
    void deleteContacts();
}
