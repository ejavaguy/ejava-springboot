package info.ejava.examples.app.testing.testbasics.tips;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.withinPercentage;
import static org.assertj.core.api.BDDAssertions.then;

@Tag("tips")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Standard Tipping Calculator")
public class StandardTippingCalculatorImplTest {
    //subject under test
    private TipCalculator tipCalculator;

    @BeforeEach
    void setup() { //simulating a complex initialization
        tipCalculator=new StandardTippingImpl();
    }
    @Test
    public void given_fair_service() {
        //given - a $100 bill with FAIR service
        BigDecimal billTotal = new BigDecimal(100);
        ServiceQuality serviceQuality = ServiceQuality.FAIR;

        //when - calculating tip
        BigDecimal resultTip = tipCalculator.calcTip(billTotal, serviceQuality);

        //then - expect a result that is 15% of the $100 total
        BigDecimal expectedTip = billTotal.multiply(BigDecimal.valueOf(0.15));
        then(resultTip).isEqualTo(expectedTip);
    }

    @ParameterizedTest
    @MethodSource
    public void given_service_level(BigDecimal billTotal, ServiceQuality q, BigDecimal expectedTip) {
        //when - calculating tip
        BigDecimal resultTip = tipCalculator.calcTip(billTotal, q);

        //then expect result within range
        then(resultTip).isCloseTo(expectedTip, withinPercentage(0.1));
    }

    static Stream<Arguments> given_service_level() {
        return Stream.of(
                Arguments.of(new BigDecimal(100), ServiceQuality.FAIR, new BigDecimal(15)),
                Arguments.of(new BigDecimal(100), ServiceQuality.GOOD, new BigDecimal(18)),
                Arguments.of(new BigDecimal(100), ServiceQuality.GREAT, new BigDecimal(20))
        );
    }
}
