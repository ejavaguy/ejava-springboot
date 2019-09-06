package info.ejava.examples.db.validation.contacts.bo;

import lombok.*;

import java.util.Comparator;

@Builder
@Value
@With
@AllArgsConstructor
public class StreetAddress implements Comparable<StreetAddress> {
    private String street;
    private String city;
    private String state;
    private String zip;

    static final Comparator<StreetAddress> ASC = (o1,o2) ->{ return o1.compareTo(o2); };
    static final Comparator<StreetAddress> DSC = (o1,o2) ->{ return o1.compareTo(o2) * -1; };

    @Override
    public int compareTo(final StreetAddress rhs) {
        int result = rhs==null ? -1 : 0;
        result = result!=0 ? result : compare(street, rhs.getStreet());
        result = result!=0 ? result : compare(city, rhs.getCity());
        result = result!=0 ? result : compare(state, rhs.getState());
        result = result!=0 ? result : compare(zip, rhs.getZip());
        return result * -1;
    }

    private static final <T extends Comparable> int compare(final T lhs, final T rhs) {
        int result = lhs!=null && rhs!=null ? rhs.compareTo(lhs) : 0;
        result = result!=0 ? result : (lhs==null) ? 1 : 0;
        result = result!=0 ? result : (rhs==null) ? -1 : 0;
        return result;
    }
}
