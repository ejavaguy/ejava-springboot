package info.ejava.examples.svc.aop.items.normalizers;

import info.ejava.examples.svc.aop.items.dto.AddressDTO;
import info.ejava.examples.svc.aop.items.dto.ContactDTO;

public class ContactDTONormalizer extends NormalizerBase {
    public ContactDTO normalize(ContactDTO contact) {
        if (contact==null) { return null; }
        
        //normalize name
        contact.setFirstName(normalizeName(contact.getFirstName()));
        contact.setLastName(normalizeName(contact.getLastName()));
        contact.getAddresses().stream().forEach(a->normalize(a));

        return contact;
    }
    
    public AddressDTO normalize(AddressDTO address) {
        if (address==null) { return null; }
        
        address.setStreet(normalizeName(address.getStreet()));
        address.setCity(normalizeName(address.getCity()));
        address.setState(toUpper(address.getState()));
        //todo normalize zip code

        return address;
    }
}
