package info.ejava.examples.db.validation.contacts.svc;

import info.ejava.examples.db.validation.contacts.bo.ContactPoint;
import info.ejava.examples.db.validation.contacts.bo.PersonPOC;
import info.ejava.examples.db.validation.contacts.bo.StreetAddress;
import info.ejava.examples.db.validation.contacts.dto.ContactPointDTO;
import info.ejava.examples.db.validation.contacts.dto.PersonPocDTO;
import info.ejava.examples.db.validation.contacts.dto.StreetAddressDTO;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContactsMapper {

    public PersonPocDTO map(PersonPOC bo) {
        PersonPocDTO dto = null;
        if (bo!=null) {
            dto = PersonPocDTO.builder()
                    .id(bo.getId()!=null ? bo.getId().toString() : null)
                    .firstName(bo.getFirstName())
                    .lastName(bo.getFirstName())
                    .dob(bo.getDob())
                    .contactPoints(mapContactPoints(bo.getContactPoints()))
                    .build();
        }
        return dto;
    }

    public PersonPOC map(PersonPocDTO dto) {
        PersonPOC bo = null;
        if (dto!=null) {
            bo = PersonPOC.builder()
                    .id(dto.getId()!=null ? new BigInteger(dto.getId()) : null)
                    .firstName(dto.getFirstName())
                    .lastName(dto.getLastName())
                    .dob(dto.getDob())
                    .contactPoints(mapContactPointsDTO(dto.getContactPoints()))
                    .build();
        }
        return bo;
    }


    public List<ContactPointDTO> mapContactPoints(Collection<ContactPoint> bos) {
        List<ContactPointDTO> dtos = null;
        if (bos!=null) {
            dtos = bos.stream().map(c->map(c)).collect(Collectors.toList());
        }
        return dtos;
    }

    public List<ContactPoint> mapContactPointsDTO(Collection<ContactPointDTO> dtos) {
        List<ContactPoint> bos = null;
        if (dtos!=null) {
            bos = dtos.stream().map(c->map(c)).collect(Collectors.toList());
        }
        return bos;
    }

    public ContactPointDTO map(ContactPoint bo) {
        ContactPointDTO dto = null;
        if (bo!=null) {
            dto = ContactPointDTO.builder()
                    .id(bo.getId())
                    .name(bo.getName())
                    .email(bo.getEmail())
                    .phone(bo.getPhoneNumber())
                    .address(map(bo.getAddress()))
                    .build();
        }
        return dto;
    }

    public ContactPoint map(ContactPointDTO dto) {
        ContactPoint bo = null;
        if (dto!=null) {
            bo = ContactPoint.builder()
                    .id(dto.getId())
                    .name(dto.getName())
                    .email(dto.getEmail())
                    .phoneNumber(dto.getPhone())
                    .address(map(dto.getAddress()))
                    .build();
        }
        return bo;
    }

    public StreetAddressDTO map(StreetAddress bo) {
        StreetAddressDTO dto = null;
        if (bo!=null) {
            dto = StreetAddressDTO.builder()
                    .street(bo.getStreet())
                    .city(bo.getCity())
                    .state(bo.getState())
                    .zip(bo.getZip())
                    .build();
        }
        return dto;
    }

    public StreetAddress map(StreetAddressDTO dto) {
        StreetAddress bo = null;
        if (dto!=null) {
            bo = StreetAddress.builder()
                    .street(dto.getStreet())
                    .city(dto.getCity())
                    .state(dto.getState())
                    .zip(dto.getZip())
                    .build();
        }
        return bo;
    }
}
