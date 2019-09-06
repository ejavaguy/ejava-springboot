package info.ejava.examples.app.testing.spock.tips

import info.ejava.examples.app.testing.testbasics.TestBasicsApp
import info.ejava.examples.app.testing.testbasics.tips.BillCalculator
import info.ejava.examples.app.testing.testbasics.tips.ServiceQuality
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Title

import static spock.util.matcher.HamcrestMatchers.closeTo

@SpringBootTest(classes=[TestBasicsApp])
@Title("bill calculator")
class BillCalculatorNSpec extends Specification {
    @Autowired
    BillCalculator billCalculator;

    def "calc shares for bill total"() {
        given:
            BigDecimal billTotal = new BigDecimal(100.0);
            ServiceQuality service = ServiceQuality.GOOD;
            BigDecimal tip = billTotal.multiply(new BigDecimal(0.18));
            int numPeople = 4;

        when: "call method under test"
            BigDecimal shareResult = billCalculator.calcShares(billTotal, service, numPeople);

        then: "verify correct result"
            BigDecimal expectedShare = billTotal.add(tip).divide(new BigDecimal(4));
            shareResult closeTo(expectedShare, new BigDecimal(0.01));
    }
}
