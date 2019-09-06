package info.ejava.examples.app.testing.testbasics.tips;

import java.math.BigDecimal;

public interface BillCalculator {
    BigDecimal calcShares(BigDecimal total, ServiceQuality service, int numPeople);
}
