package org.book.account.core;

import org.apache.commons.lang3.Validate;
import org.book.account.domain.Amount;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "closure_threshold_performance_indicators")
public class AccountClosureThresholdPerformanceIndicator {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    private Amount threshold;
    private Date validUntil;
    private boolean reachThresholdEveryDay;
    @ManyToOne
    @JoinColumn(name = "ledger_id")
    private Ledger ledger;

    public AccountClosureThresholdPerformanceIndicator() {
    }

    public AccountClosureThresholdPerformanceIndicator(Ledger ledger, String name, Amount threshold, Account account, Date validUntil, boolean reachThresholdEveryDay) {
        Validate.notNull(name, "The name must not be null");
        Validate.notNull(threshold, "threshold cannot be null");
        Validate.notNull(account, "account cannot be null");
        Validate.notNull(validUntil, "validUntil cannot be null");
        Validate.notNull(ledger, "ledger cannot be null");

        this.ledger = ledger;
        this.name = name;
        this.threshold = threshold;
        this.validUntil = validUntil;
        this.account = account;
        this.reachThresholdEveryDay = reachThresholdEveryDay;
    }

    public Ledger getLedger() {
        return ledger;
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
