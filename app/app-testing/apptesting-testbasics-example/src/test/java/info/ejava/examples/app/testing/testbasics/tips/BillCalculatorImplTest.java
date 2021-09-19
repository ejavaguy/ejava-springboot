package info.ejava.examples.app.testing.testbasics.tips;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.BDDAssertions.and;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

/**
 * This example unit test leverages Mockito to instantiate the subject(s) under test
 * and inject Mocks into those instances.
 */

@Tag("tips")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Bill CalculatorImpl")
@ExtendWith(MockitoExtension.class)
public class BillCalculatorImplTest {
    @Mock
    TipCalculator tipCalculatorMock;

    /*
    Mockito is instantiating this implementation class for us an injecting Mocks
     */
    @InjectMocks
    BillCalculatorImpl billCalculator;

    @Test
    public void calc_shares_for_people_including_tip() {
        //given - we have a bill for 4 people and tip calculator that returns amount for tip
        BigDecimal billTotal = new BigDecimal(100.0);
        ServiceQuality service = ServiceQuality.GOOD;
        BigDecimal tip = new BigDecimal(50.00); //50% tip!!!
        int numPeople = 4;
        //configure mock
        given(tipCalculatorMock.calcTip(billTotal, service)).willReturn(tip);

        //when - call method under test
        BigDecimal result = billCalculator.calcShares(billTotal, service, numPeople);

        //then - verify tip calculator called once to get result
        then(tipCalculatorMock).should(times(1)).calcTip(billTotal,service);
        //and then verify correct result
        BigDecimal expectedShare = billTotal.add(tip).divide(new BigDecimal(numPeople));
        and.then(result).isEqualTo(expectedShare);
    }
}
