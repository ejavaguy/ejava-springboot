package info.ejava.examples.app.testing.spock.tips

import info.ejava.examples.app.testing.testbasics.tips.BillCalculator
import info.ejava.examples.app.testing.testbasics.tips.BillCalculatorImpl
import info.ejava.examples.app.testing.testbasics.tips.ServiceQuality
import info.ejava.examples.app.testing.testbasics.tips.TipCalculator
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Title

import static spock.util.matcher.HamcrestMatchers.closeTo

@SpringBootTest(classes=[BillCalculatorImpl])
@Title("Bill CalculatorImpl Mocked Integration")
class BillCalculatorMockedNSpec extends Specification {
    @Autowired //subject under test
    BillCalculator billCalculator;

    @SpringBean  //replaces Spring's @MockBean
    TipCalculator tipCalculatorMock = Mock()

    def "calc shares for people including tip"() {
        given: "we have a bill for 4 people and tip calculator that returns tip amount"
            BigDecimal billTotal = new BigDecimal(100.0);
            ServiceQuality service = ServiceQuality.GOOD;
            BigDecimal tip = billTotal.multiply(new BigDecimal(0.18));
            int numPeople = 4;

        when: "call method under test"
            BigDecimal shareResult = billCalculator.calcShares(billTotal, service, numPeople);

        then: "tip calculator should be called once to get result"
            1 * tipCalculatorMock.calcTip(billTotal, service) >> tip;

        and: "verify correct result"
            BigDecimal expectedShare = billTotal.add(tip).divide(new BigDecimal(numPeople));
            shareResult closeTo(expectedShare, new BigDecimal(0.01));
    }
}
