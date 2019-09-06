package info.ejava.examples.app.testing.spock.tips

import groovy.util.logging.Slf4j
import info.ejava.examples.app.testing.testbasics.tips.ServiceQuality
import info.ejava.examples.app.testing.testbasics.tips.StandardTippingImpl
import info.ejava.examples.app.testing.testbasics.tips.TipCalculator
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll

import static spock.util.matcher.HamcrestMatchers.closeTo

@Slf4j
@Title("Standard Tipping Calculator")
class StandardTippingCalculatorImplSpec extends Specification {
    TipCalculator tipCalculator;

    def setup() {
        tipCalculator = new StandardTippingImpl();
    }
    def "given fair service" () {
        given: "a \$100 bill with FAIR service"
        BigDecimal billTotal = BigDecimal.valueOf(100)
        ServiceQuality serviceQuality = ServiceQuality.FAIR

        when: "calculating tip"
        BigDecimal resultTip = tipCalculator.calcTip(billTotal, serviceQuality)

        then: "expect a result that is 15% of the \$100 total"
        BigDecimal expectedTip = billTotal.multiply(BigDecimal.valueOf(0.15))

        and:
        resultTip == expectedTip
    }

    @Unroll
    def "given service level (#billTotal, #q, #expectedTip)"() {
        when: "calculating tip"
        BigDecimal resultTip = tipCalculator.calcTip(billTotal, q);

        then: "expect result within range"
        resultTip closeTo(expectedTip, new BigDecimal(0.1))

        where:
        billTotal << [new BigDecimal(100), new BigDecimal(100), new BigDecimal(100)]
        q << [ ServiceQuality.FAIR, ServiceQuality.GOOD, ServiceQuality.GREAT]
        expectedTip << [new BigDecimal(15), new BigDecimal(18), new BigDecimal(20)]
    }

    @Unroll
    def "given service level (table: #billTotal, #q, #expectedTip)"() {//billTotal, q, expectedTip) {
        when: "calculating tip"
        BigDecimal resultTip = tipCalculator.calcTip(billTotal, q);

        then: "expect result within range"
        resultTip closeTo(expectedTip, new BigDecimal(0.1))

        where:
        billTotal           | q                     || expectedTip
        new BigDecimal(200) | ServiceQuality.FAIR   || new BigDecimal(30)
        new BigDecimal(200) | ServiceQuality.GOOD   || new BigDecimal(36)
        new BigDecimal(200) | ServiceQuality.GREAT  || new BigDecimal(40)
    }
}
