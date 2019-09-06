package info.ejava.examples.svc.aop.items.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private String street;
    private String city;
    private String state;
    private String zip;
}
