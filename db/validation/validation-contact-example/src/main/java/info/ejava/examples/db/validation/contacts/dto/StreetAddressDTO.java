package info.ejava.examples.db.validation.contacts.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import info.ejava.examples.db.validation.contacts.constraints.PostalStateAbbreviation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import static info.ejava.examples.db.validation.contacts.dto.ValidationNamespace.VALIDATION_NAMESPACE;

@JacksonXmlRootElement(localName = "streetAddress", namespace = VALIDATION_NAMESPACE)
@XmlRootElement(name = "book", namespace = VALIDATION_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreetAddressDTO {
    @Pattern(regexp = "^[0-9]{1,}.+[A-Za-z]$")
    private String street;
    @Pattern(regexp="^[A-Za-z \'-]{2,}$")
    private String city;
    @PostalStateAbbreviation(normalize = false)
    private String state;
    @Pattern(regexp="^[0-9]{5}(-{0,1}[0-9]{4}){0,1}$")
    private String zip;
}
