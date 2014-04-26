package org.book.account.core;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.book.account.domain.*;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "budget")
class Budget implements IBudget {
    private static final Logger LOG = LogManager.getLogger(Ledger.class.getName());
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "planned_transactions",
            joinColumns = @JoinColumn(name = "id")
    )
    private List<PlannedTransaction> plannedTransactions = new LinkedList<PlannedTransaction>();
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private Ledger ledger;

    // required by hibernate
    Budget() {

    }

    public Budget(Ledger associatedLedger) {
        Validate.notNull(associatedLedger, "associatedLedger cannot be null");
        this.ledger = associatedLedger;
    }

    public Ledger getLedger() {
        return ledger;
    }

    public List<IPlannedTransaction> getPlannedTransactions() {
        return new LinkedList<IPlannedTransaction>(plannedTransactions);
    }

    public Amount forecast(IAccount account, IAccount relativeTo, Date forecastOn, MatchingPolicy matchingPolicy) {
        Validate.notNull(account, "account cannot be null");
        Validate.notNull(relativeTo, "relativeTo cannot be null");
        Validate.notNull(matchingPolicy, "matchingPolicy cannot be null");
        Validate.notNull(forecastOn, "forecastOn cannot be null");

        Date now = new Date();
        if (forecastOn.before(now)) {
            SimpleDateFormat sdf = new SimpleDateFormat();
            LOG.info("trying to forecast on a date (" + sdf.format(forecastOn) + ") before now (" + sdf.format(now) + "). Returning the closure at that time (the best forecast of what closure the account had then).");
            return account.closure(forecastOn);
        }

        List<ITransaction> relevantTransactions = new LinkedList<ITransaction>();
        for (ITransaction transaction : account.getTransactions()) {
            if (transaction.getCreditor().equals(relativeTo) || transaction.getDebitor().equals(relativeTo)) {
                relevantTransactions.add(transaction);
            }
        }

        Amount expectedClosure = sumPlannedTransactions(account, forecastOn, matchingPolicy, now, relevantTransactions);
        return Amount.add(account.closure(now, relativeTo), expectedClosure);
    }

    public Amount forecast(IAccount account, Date forecastOn, MatchingPolicy matchingPolicy) {
        Validate.notNull(account, "account cannot be null");
        Validate.notNull(matchingPolicy, "matchingPolicy cannot be null");
        Validate.notNull(forecastOn, "forecastOn cannot be null");

        Date now = new Date();
        if (forecastOn.before(now)) {
            SimpleDateFormat sdf = new SimpleDateFormat();
            LOG.info("trying to forecast on a date (" + sdf.format(forecastOn) + ") before now (" + sdf.format(now) + "). Returning the closure at that time (the best forecast of what closure the account had then).");
            return account.closure(forecastOn);
        }

        Amount expectedClosure = sumPlannedTransactions(account, forecastOn, matchingPolicy, now, account.getTransactions());
        return Amount.add(account.closure(now), expectedClosure);
    }

    private Amount sumPlannedTransactions(IAccount account, Date forecastOn, MatchingPolicy matchingPolicy, Date now, List<ITransaction> transactions) {
        Amount expectedClosure = Amount.noAmount();
        Period forecastPeriod = new Period(now, forecastOn);
        for (IPlannedTransaction plannedTransaction : account.getPlannedTransactions()) {
            boolean foundMatchingTransaction = false;
            for (ITransaction transaction : transactions) {
                if (matchingPolicy.match(transaction, plannedTransaction)) {
                    foundMatchingTransaction = true;
                }
            }

            if (!foundMatchingTransaction) {
                Amount forecastOfPlannedTransaction = plannedTransaction.forecast(forecastPeriod);

                if (account.equals(plannedTransaction.getCreditor())) {
                    expectedClosure = Amount.add(expectedClosure, forecastOfPlannedTransaction);
                } else {
                    expectedClosure = Amount.subtract(expectedClosure, forecastOfPlannedTransaction);
                }
            }
        }
        return expectedClosure;
    }

    public IPlannedTransaction plan(String narration, Amount amount, IAccount debitor, IAccount creditor, Schedule schedule) {
        getLedger().assertThatAccountExists((Account) debitor);
        getLedger().assertThatAccountExists((Account) creditor);

        PlannedTransaction newTransaction = new PlannedTransaction(narration, amount, (Account) debitor, (Account) creditor, schedule);
        plannedTransactions.add(newTransaction);
        return newTransaction;
    }

    public void remove(PlannedTransaction transaction) {
        plannedTransactions.remove(transaction);
    }
}
