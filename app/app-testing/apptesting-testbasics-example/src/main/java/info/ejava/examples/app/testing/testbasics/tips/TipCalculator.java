package info.ejava.examples.app.testing.testbasics.tips;

import java.math.BigDecimal;

public interface TipCalculator {
    public BigDecimal calcTip(BigDecimal amount, ServiceQuality serviceQuality);
}
