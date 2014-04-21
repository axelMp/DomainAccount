package org.book.account.core;

import org.apache.commons.lang3.Validate;
import org.book.account.domain.*;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "budget")
class Budget implements IBudget {
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

    public Amount forecast(IAccount account, Date forecastOn) {
        Validate.notNull(forecastOn, "forecastOn cannot be null");
        List<ITransaction> transactions = account.getTransactions();
        Date today = new Date();
        Amount expectedClosure = Amount.noAmount();

        for (IPlannedTransaction plannedTransaction : ((Account) account).getPlannedTransactions()) {
            if (!plannedTransaction.matchesAnyPerformedTransaction(transactions)) {
                Amount forecastOfPlannedTransaction = plannedTransaction.forecast(today, forecastOn);

                if (((Account) account).equals(plannedTransaction.getCreditor())) {
                    expectedClosure = Amount.add(expectedClosure, forecastOfPlannedTransaction);
                } else {
                    expectedClosure = Amount.subtract(expectedClosure, forecastOfPlannedTransaction);
                }
            }
        }
        return Amount.add(account.closure(today), expectedClosure);
    }

    public IPlannedTransaction plan(String narration, Date startsOn, Date endsOn, Amount amount, IAccount debitor, IAccount creditor, ExecutionOfPlannedTransaction executionOfPlannedTransaction) {
        getLedger().assertThatAccountExists((Account) debitor);
        getLedger().assertThatAccountExists((Account) creditor);

        PlannedTransaction newTransaction = new PlannedTransaction(narration, amount, (Account) debitor, (Account) creditor, startsOn, endsOn, executionOfPlannedTransaction);
        plannedTransactions.add(newTransaction);
        return newTransaction;
    }

    public void remove(PlannedTransaction transaction) {
        plannedTransactions.remove(transaction);
    }
}
