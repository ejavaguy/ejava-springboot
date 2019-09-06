package info.ejava.examples.app.testing.testbasics;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class PeopleFactory {
    public static Person beaver() {
        return new Person("Jerry", "Mathers",
                new GregorianCalendar(1948, Calendar.JUNE, 2).getTime());
    }

    public static Person wally() {
        return new Person("Tony", "Dow",
                new GregorianCalendar(1945, Calendar.APRIL, 13).getTime());
    }

    public static Person eddie() {
        return new Person("Ken", "Osmond",
                new GregorianCalendar(1943, Calendar.JUNE, 7).getTime());
    }
}
