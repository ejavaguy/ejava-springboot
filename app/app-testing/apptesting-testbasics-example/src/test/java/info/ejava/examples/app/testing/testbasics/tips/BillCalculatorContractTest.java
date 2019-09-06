package info.ejava.examples.app.testing.testbasics.tips;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@Tag("tips")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Bill Calculator Contract")
@ExtendWith(MockitoExtension.class)
public class BillCalculatorContractTest {
    @Mock
    private BillCalculator billCalculatorMock;

    @Test
    public void calc_shares_for_people_including_tip() {
        //given - billCalculator will return the value for a share
        given(billCalculatorMock.calcShares(any(), any(), anyInt())).willReturn(BigDecimal.valueOf(1));
        //when
        BigDecimal share = billCalculatorMock.calcShares(BigDecimal.valueOf(100), ServiceQuality.GOOD, 2);
        //then
        then(share).isNotNull();
    }
}
