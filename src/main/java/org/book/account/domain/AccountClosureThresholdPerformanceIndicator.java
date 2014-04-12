package org.book.account.domain;

import java.util.Date;

public class AccountClosureThresholdPerformanceIndicator extends Indicator<Amount> {

    public AccountClosureThresholdPerformanceIndicator(String name,Amount threshold,Account account,Date validUntil,boolean reachThresholdEveryDay) {
        super(name);

        if ( null == threshold ) {
            throw new IllegalArgumentException("threshold cannot be null");
        }
        this.threshold = threshold;

        if ( null == validUntil ) {
            throw new IllegalArgumentException("validUntil cannot be null");
        }
        this.validUntil = validUntil;

        if ( null == account ) {
            throw new IllegalArgumentException("account cannot be null");
        }
        this.account = account;

        this.reachThresholdEveryDay = reachThresholdEveryDay;
    }

    public Amount indicatorValueAt(Date aDate) {
        return account.closure(aDate);
    }

    public Amount expectedValueAt(Date aDate) {
        if ( reachThresholdEveryDay ) {
            return threshold;
        } else if (aDate.equals(validUntil)) {
            return threshold;
        } else {
            return Amount.noAmount();
        }
    }

    private final Account account;
    private final Amount threshold;
    private final Date validUntil;
    private final boolean reachThresholdEveryDay;
}
