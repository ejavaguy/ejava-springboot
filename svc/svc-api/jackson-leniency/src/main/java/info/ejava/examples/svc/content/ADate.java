package info.ejava.examples.svc.content;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@With
public class ADate {
    private ZonedDateTime zdt;
    private OffsetDateTime odt;
    private LocalDateTime ldt;
    private Instant instant;
    private Date date;

    public static ADate of(ZonedDateTime zdt) {
        return new ADate(zdt, zdt.toOffsetDateTime(), zdt.toLocalDateTime(), zdt.toInstant(), Date.from(zdt.toInstant()));
    }

    public ADate truncateDateToMillis() {
        Date milliDate = Date.from(date.toInstant().truncatedTo(ChronoUnit.MILLIS));
        return of(zdt).withDate(milliDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ADate)) return false;
        ADate aDate = (ADate) o;

        if (zdt != null ? !zdt.isEqual(aDate.zdt) : aDate.zdt != null) return false;
        if (odt != null ? !odt.isEqual(aDate.odt) : aDate.odt != null) return false;
        //LDT will be in a specific timezone, without a reference to that TZ -- so will be false if not same TZ
        //if (ldt != null ? !ldt.isEqual(aDate.ldt) : aDate.ldt != null) return false;
        if (instant != null ? !instant.equals(aDate.instant) : aDate.instant != null) return false;
        return date != null ? date.equals(aDate.date) : aDate.date == null;
    }

    @Override
    public int hashCode() {
        int result = zdt != null ? zdt.hashCode() : 0;
        result = 31 * result + (odt != null ? odt.hashCode() : 0);
        //result = 31 * result + (ldt != null ? ldt.hashCode() : 0);
        result = 31 * result + (instant != null ? instant.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
