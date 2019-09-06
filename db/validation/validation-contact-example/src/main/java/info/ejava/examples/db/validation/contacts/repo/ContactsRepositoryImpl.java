package info.ejava.examples.db.validation.contacts.repo;

import info.ejava.examples.db.validation.contacts.bo.ContactPoint;
import info.ejava.examples.db.validation.contacts.bo.PersonPOC;
import org.springframework.data.mapping.callback.EntityCallback;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveCallback;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ContactsRepositoryImpl implements BeforeConvertCallback<PersonPOC>{
    /**
     * MongoRepository only assigns a unique ID to the root Document. We must assign
     * all nested child IDs. This callback is registered to handle change events
     * when the root document with children are inserted or saved.
     * @param poc
     * @param collection
     * @return poc with child IDs assigned
     */
    @Override
    public PersonPOC onBeforeConvert(PersonPOC poc, String collection) {
        if (poc!=null && poc.getContactPoints()!=null) {
            nameContactPoints(poc.getContactPoints());
        }
        return poc;
    }

    private void nameContactPoints(Collection<ContactPoint> contactPoints) {
        Map<String, Integer> nameCounts = new HashMap<>();
        for (ContactPoint cp: contactPoints) {
            Integer count = nameCounts.get(cp.getName());
            if (count==null) {
                count = Integer.valueOf(1);
            }
            nameCounts.put(cp.getName(), count + 1);
        }

        //group by common contact name
        Map<String, List<ContactPoint>> byName = contactPoints.stream()
                .collect(Collectors.groupingBy(
                        ContactPoint::getName,
                        Collectors.mapping(Function.identity(), Collectors.toList())
                        )
                );


        //set ID to name plus a unique mangler for this POC
        for (Map.Entry<String, List<ContactPoint>> entry: byName.entrySet()) {
            final String name = entry.getKey();
            final List<ContactPoint> points = entry.getValue();
            final boolean multiple = entry.getValue().size()>1;
            if (multiple) {
                Collections.sort(points);
            }

            int idx=0;
            for (ContactPoint cp: points) {
                String id = multiple ? (name + "-" + idx++) : name;
                cp.setId(id);
            }
        }
    }
}
