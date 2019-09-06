package info.ejava.examples.svc.aop.items.services;

import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava.examples.svc.aop.items.dto.AddressDTO;
import info.ejava.examples.svc.aop.items.dto.ContactDTO;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ContactsServiceImpl implements ContactsService {
    private AtomicInteger nextId = new AtomicInteger(1);
    private ConcurrentMap<Integer, ContactDTO> contacts = new ConcurrentHashMap();

    @Override
    public ContactDTO createContact(ContactDTO contact) {
        contact.setContectId(nextId.getAndAdd(1));
        contacts.put(contact.getContectId(), contact);
        return contact;
    }

    @Override
    public ContactDTO updateContact(int id, ContactDTO contact) {
        if (!contacts.containsKey(id)) {
           throw new ClientErrorException.NotFoundException("contact[%d] not found", id);
        }
        contacts.put(id, contact);
        return contact;
    }

    @Override
    public ContactDTO getContact(int id) {
        ContactDTO contact = contacts.get(id);
        if (contact==null) {
            throw new ClientErrorException.NotFoundException("contact[%d] not found", id);
        }
        return contact;
    }

    @Override
    public ContactDTO addAddress(int id, AddressDTO address) {
        ContactDTO contact = getContact(id);
        contact.getAddresses().add(address);
        return contact;
    }

    @Override
    public void deleteContact(int id) {
        contacts.remove(id);
    }

    @Override
    public void deleteContacts() {
        contacts.clear();
    }
}
