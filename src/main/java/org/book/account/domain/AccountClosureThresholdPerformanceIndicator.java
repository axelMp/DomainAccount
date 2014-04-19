package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import java.util.Date;

public class AccountClosureThresholdPerformanceIndicator {

    private String name;
    private Account account;
    private Amount threshold;
    private Date validUntil;
    private boolean reachThresholdEveryDay;

    public AccountClosureThresholdPerformanceIndicator() {
    }

    public AccountClosureThresholdPerformanceIndicator(String name, Amount threshold, Account account, Date validUntil, boolean reachThresholdEveryDay) {
        Validate.notNull(name, "The name must not be null");
        Validate.notNull(threshold, "threshold cannot be null");
        Validate.notNull(account, "account cannot be null");
        Validate.notNull(validUntil, "validUntil cannot be null");

        this.name = name;
        this.threshold = threshold;
        this.validUntil = validUntil;
        this.account = account;
        this.reachThresholdEveryDay = reachThresholdEveryDay;
    }

    public String getName() {
        return name;
    }

    public Amount indicatorValueAt(Date aDate) {
        return account.closure(aDate);
    }

    public Amount expectedValueAt(Date aDate) {
        if (reachThresholdEveryDay) {
            return threshold;
        } else if (aDate.equals(validUntil)) {
            return threshold;
        } else {
            return Amount.noAmount();
        }
    }
}
