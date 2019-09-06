package info.ejava.examples.db.validation.contacts.bo;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;

@Data
@Builder
public class ContactPoint implements Comparable<ContactPoint> {
    @EqualsExclude
    private String id;
    @NotNull
    private String name;
    private String email;
    @Min(10)
    private String phoneNumber;
    private StreetAddress address;

    static final Comparator<ContactPoint> ASC = (o1,o2) ->{ return o1.compareTo(o2); };
    static final Comparator<ContactPoint> DSC = (o1,o2) ->{ return o1.compareTo(o2) * -1; };

    @Override
    public int compareTo(ContactPoint rhs) {
        int result = rhs==null ? 1 : 0;
        result = result!=0 ? result : compare(name, rhs.getName());
        result = result!=0 ? result : compare(email, rhs.getEmail());
        result = result!=0 ? result : compare(phoneNumber, rhs.getPhoneNumber());
        result = result!=0 ? result : compare(address, rhs.getAddress());

        return result * -1;
    }

    private static final <T extends Comparable> int compare(final T lhs, final T rhs) {
        int result = lhs!=null && rhs!=null ? rhs.compareTo(lhs) : 0;
        result = result!=0 ? result : (lhs==null) ? 1 : 0;
        result = result!=0 ? result : (rhs==null) ? -1 : 0;
        return result;
    }
}
