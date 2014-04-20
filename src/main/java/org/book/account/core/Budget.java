package org.book.account.core;

import org.apache.commons.lang3.Validate;
import org.book.account.domain.*;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "budget")
public class Budget implements IBudget {
    @OneToMany
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
    public Budget() {

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
