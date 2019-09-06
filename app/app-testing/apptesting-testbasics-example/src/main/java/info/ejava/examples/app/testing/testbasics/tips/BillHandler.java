package info.ejava.examples.app.testing.testbasics.tips;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;

@Component
@Slf4j
@RequiredArgsConstructor
public class BillHandler implements CommandLineRunner {
    private final NumberFormat mf = NumberFormat.getCurrencyInstance();
    private final BillCalculator billCalculator;

    @Override
    public void run(String... args) throws Exception {
        BigDecimal billTotal = new BigDecimal(50);
        ServiceQuality serviceQuality = ServiceQuality.GOOD;
        int numPeople = 4;
        BigDecimal share=billCalculator.calcShares(billTotal, serviceQuality, numPeople);
        log.info("bill total {}, share={} for {} people, after adding tip for {} service",
                mf.format(billTotal), mf.format(share), numPeople, serviceQuality);
    }
}
