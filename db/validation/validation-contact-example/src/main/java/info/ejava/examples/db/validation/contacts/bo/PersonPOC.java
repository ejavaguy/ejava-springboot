package info.ejava.examples.db.validation.contacts.bo;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "pocs")
@Builder
@Data
public class PersonPOC {
    @Id
    private BigInteger id;

    private String firstName;
    private String lastName;
    @NotNull
    @Past
    private LocalDate dob;
    private List<ContactPoint> contactPoints;
}
