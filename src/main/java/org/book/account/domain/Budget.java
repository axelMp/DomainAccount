package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Budget {
    @OneToMany
    private List<PlannedTransaction> plannedTransactions = new LinkedList<PlannedTransaction>();
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "LEDGER")
    private Ledger ledger;

    public Budget(Ledger associatedLedger) {
        Validate.notNull(associatedLedger, "associatedLedger cannot be null");
        this.ledger = associatedLedger;
    }

    public List<PlannedTransaction> getPlannedTransactions() {
        return plannedTransactions;
    }

    public PlannedTransaction plan(String text, Date startsOn, Date endsOn, Amount amount, Account from, Account to, boolean isContinuous) {
        ledger.assertThatAccountExists(from);
        ledger.assertThatAccountExists(to);

        PlannedTransaction newTransaction = new PlannedTransaction(text, amount, from, to, startsOn, endsOn, isContinuous);
        plannedTransactions.add(newTransaction);
        return newTransaction;
    }

    public void remove(PlannedTransaction transaction) {
        plannedTransactions.remove(transaction);
    }


}
