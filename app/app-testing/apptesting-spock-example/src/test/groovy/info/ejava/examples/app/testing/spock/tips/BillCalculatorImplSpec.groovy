package info.ejava.examples.app.testing.spock.tips

import info.ejava.examples.app.testing.testbasics.tips.BillCalculatorImpl
import info.ejava.examples.app.testing.testbasics.tips.ServiceQuality
import info.ejava.examples.app.testing.testbasics.tips.TipCalculator
import spock.lang.Specification
import spock.lang.Title

import static spock.util.matcher.HamcrestMatchers.closeTo

@Title("Bill CalculatorImpl")
class BillCalculatorImplSpec extends Specification {
    TipCalculator tipCalculatorMock = Mock();
    BillCalculatorImpl billCalculator;

    def setup() {
        billCalculator = new BillCalculatorImpl(tipCalculatorMock);
    }

    def "calc shares for people including tip"() {
        given: "we have a bill for 4 people and tip calculator that returns amount for tip"
            BigDecimal billTotal = new BigDecimal(100.0);
            ServiceQuality service = ServiceQuality.GOOD;
            BigDecimal tip = new BigDecimal(50.00); //50% tip!!!
            int numPeople = 4;
            //stub behavior of mock defined in then block

        when: "call method under test"
            BigDecimal result = billCalculator.calcShares(billTotal, service, numPeople);

        then: "verify tip calculator called once to get result"
            1 * tipCalculatorMock.calcTip(billTotal, service) >> tip
         and: "then verify correct result"
            BigDecimal expectedShare = billTotal.add(tip).divide(new BigDecimal(numPeople));
            result closeTo(expectedShare, new BigDecimal(0.01))
    }
}
