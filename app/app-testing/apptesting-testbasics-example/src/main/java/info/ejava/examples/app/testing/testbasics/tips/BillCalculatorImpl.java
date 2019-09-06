package info.ejava.examples.app.testing.testbasics.tips;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;

@Component
@Slf4j
@RequiredArgsConstructor
public class BillCalculatorImpl implements BillCalculator {
    private static final boolean DEBUG_ENABLED = log.isDebugEnabled();
    private final NumberFormat mf = NumberFormat.getCurrencyInstance();

    private final TipCalculator tipCalculator;

    @Override
    public BigDecimal calcShares(BigDecimal total, ServiceQuality service, int numPeople) {
        //calc tip
        BigDecimal tip = tipCalculator.calcTip(total, service);
        if (DEBUG_ENABLED) {
            log.debug("tip={}, for {} and {} service", mf.format(tip), mf.format(total), service);
        }

        //determine overall total
        BigDecimal overallTotal = total.add(tip);

        //calc and return per-share value
        BigDecimal share = overallTotal.divide(BigDecimal.valueOf(numPeople));
        if (DEBUG_ENABLED) {
            log.debug("share={} for {}, {} people and {} service", mf.format(share), mf.format(total), numPeople, service);
        }
        return share;
    }
}
