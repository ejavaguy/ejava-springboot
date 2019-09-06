package info.ejava.examples.svc.aop.items.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ContactDTO {
    private int contectId;
    private String firstName;
    private String lastName;
    private List<AddressDTO> addresses = new ArrayList<>();
}
