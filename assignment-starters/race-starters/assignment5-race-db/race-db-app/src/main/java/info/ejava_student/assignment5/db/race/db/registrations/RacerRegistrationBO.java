package info.ejava_student.assignment5.db.race.db.registrations;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

//@NamedQuery(name= RacerRegistrationBO.FIND_BY_AGE_RANGE_QUERY,
//        query="select r from RacerRegistrationBO r where r.age between :min and :max order by r.id ASC")
public class RacerRegistrationBO {
    public static final String TABLE_NAME="race_registration";
    public static final String FIND_BY_AGE_RANGE_QUERY = "RacerRegistrationBO.findByAgeRange";

//    @PrePersist
    void prePersist() {
        if (id==null) {
            id= UUID.randomUUID().toString();
        }
    }

    private String id;
//    @Column(name="RACE_ID")
    private String raceId;
    private String racerId;
    private String raceName;
    private LocalDate raceDate;
    private String firstName;
    private String lastName;
    private String gender;
    private int age;
    private String ownername;
}
