package info.ejava.examples.app.testing.testbasics.tips;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.BDDAssertions.and;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

/**
 * This test case provides an example of implementing a unit test with
 * Mocks and without a Spring Context and without using Mockito
 * to instantiate the subjects with Mocks. We do this by hand.
 */

@ExtendWith(MockitoExtension.class)
@Tag("tips")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Bill CalculatorImpl Mocked Unit Test")
public class BillCalculatorMockedTest {
    //subject under test
    private BillCalculator billCalculator;

    @Mock
    private TipCalculator tipCalculatorMock;

    @BeforeEach
    void init() {
        billCalculator = new BillCalculatorImpl(tipCalculatorMock);
    }

    @Test
    public void calc_shares_for_people_including_tip() {
        //given - we have a bill for 4 people and tip calculator that returns tip amount
        BigDecimal billTotal = new BigDecimal(100.0);
        ServiceQuality service = ServiceQuality.GOOD;
        BigDecimal tip = billTotal.multiply(new BigDecimal(0.18));
        int numPeople = 4;
        //configure mock
        given(tipCalculatorMock.calcTip(billTotal, service)).willReturn(tip);

        //when - call method under test
        BigDecimal shareResult = billCalculator.calcShares(billTotal, service, numPeople);

        //then - tip calculator should be called once to get result
        then(tipCalculatorMock).should(times(1)).calcTip(billTotal,service);

        //verify correct result
        BigDecimal expectedShare = billTotal.add(tip).divide(new BigDecimal(numPeople));
        and.then(shareResult).isEqualTo(expectedShare);
    }
}
