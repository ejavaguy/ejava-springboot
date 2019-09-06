package info.ejava.examples.db.validation.contacts.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.*;
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
public class ContactPointDTO {
    @Null (groups = {PocValidationGroups.Create.class},
            message = "cannot be specified for create")
    private String id;
    @NotNull
    private String name;
    @Size(min=7, max=40, groups= PocValidationGroups.SimplePlusDefault.class)
    @Email(groups = PocValidationGroups.DetailedOnly.class)
    private String email;
    @Size(min=8, message = "${validatedValue} must have {min} characters")
    @Pattern(regexp = ".*[0-9]{3}[- \\.][0-9]{4}.*",
            message = "${formatter.format('>>%10s<<', validatedValue)} must contain (3 digit)-(4 digit) number")
    private String phone;
    @Valid
    private StreetAddressDTO address;

    @Valid
    public ContactPointDTO(@NotNull @Named("id") String id, String name, String email, String phone) {
        this.id=id;
        this.name=name;
        this.email=email;
        this.phone=phone;
    }
}
