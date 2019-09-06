package info.ejava.examples.db.validation.contacts.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.Date;

public class MinAgeDateValidator implements ConstraintValidator<MinAge, Date> {
    private int minAge;
    private ZoneOffset zoneOffset;

    @Override
    public void initialize(MinAge annotation) {
        if (annotation.age() < 0) {
            throw new IllegalArgumentException("age constraint cannot be negative");
        }
        this.minAge = annotation.age();

        if (annotation.tzOffsetHours() > 23 || annotation.tzOffsetHours() < -23) {
            throw new IllegalArgumentException("tzOffsetHours must be between -23 and +23");
        }
        zoneOffset = ZoneOffset.ofHours(annotation.tzOffsetHours());
    }

    @Override
    public boolean isValid(Date dob, ConstraintValidatorContext context) {
        if (dob!=null) { //assume null is valid and use @NotNull if it should not be
            final LocalDate dobLD = LocalDate.ofInstant(dob.toInstant(), zoneOffset);
            final LocalDate now = LocalDate.now(zoneOffset);
            final int currentAge = Period.between(dobLD, now).getYears();
            return currentAge >= minAge;
        }
        return true;
    }
}
