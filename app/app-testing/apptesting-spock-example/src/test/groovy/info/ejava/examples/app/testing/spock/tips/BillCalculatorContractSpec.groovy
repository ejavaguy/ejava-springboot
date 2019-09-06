package info.ejava.examples.app.testing.spock.tips

import info.ejava.examples.app.testing.testbasics.tips.BillCalculator
import info.ejava.examples.app.testing.testbasics.tips.ServiceQuality
import spock.lang.Specification
import spock.lang.Title

import static spock.util.matcher.HamcrestMatchers.closeTo

@Title("Bill Calculator Contract")
class BillCalculatorContractSpec extends Specification {
    BillCalculator billCalculatorMock = Mock(BillCalculator)

    def "calc shares for people including tip"() {
        given: "bill total, service quality, and number of people"
        when: "calculating a share"
            BigDecimal share = billCalculatorMock.calcShares(BigDecimal.valueOf(100), ServiceQuality.GOOD, 4)

        then: "will produce a share value"
            1 * billCalculatorMock.calcShares(_, _, _) >> new BigDecimal(1); //mock and stub
        and: "for good measure, we can evaluate that share value"
            share closeTo(new BigDecimal(1), new BigDecimal((0.01)))
    }

}
