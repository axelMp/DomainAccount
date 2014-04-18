package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "budget")
public class Budget {
    @OneToMany(mappedBy = "id")
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

    public List<PlannedTransaction> getPlannedTransactions() {
        return plannedTransactions;
    }

    public PlannedTransaction plan(String narration, Date startsOn, Date endsOn, Amount amount, Account debitor, Account creditor, boolean isContinuous) {
        ledger.assertThatAccountExists(debitor);
        ledger.assertThatAccountExists(creditor);

        PlannedTransaction newTransaction = new PlannedTransaction(narration, amount, debitor, creditor, startsOn, endsOn, isContinuous);
        plannedTransactions.add(newTransaction);
        return newTransaction;
    }

    public void remove(PlannedTransaction transaction) {
        plannedTransactions.remove(transaction);
    }


}
