package org.book.account.core;

import org.apache.commons.lang3.Validate;
import org.book.account.domain.Amount;
import org.book.account.domain.ExecutionOfPlannedTransaction;
import org.book.account.domain.IBudget;
import org.book.account.domain.IPlannedTransaction;

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

    List<PlannedTransaction> getPlannedTransactions(Account anAccount) {
        List<PlannedTransaction> plannedTransactions = new LinkedList<PlannedTransaction>();
        for (IPlannedTransaction plannedTransaction : getPlannedTransactions()) {
            if (anAccount.equals(((PlannedTransaction) plannedTransaction).getCreditor()) || anAccount.equals(((PlannedTransaction) plannedTransaction).getDebitor())) {
                plannedTransactions.add(((PlannedTransaction) plannedTransaction));
            }
        }

        return plannedTransactions;
    }

    public PlannedTransaction planLinearlyProgressingTransaction(String narration, Date startsOn, Date endsOn, Amount amount, Account debitor, Account creditor) {
        return plan(narration, startsOn, endsOn, amount, debitor, creditor, ExecutionOfPlannedTransaction.LINEARLY_PROGRESSING);
    }

    public PlannedTransaction planSingleTransaction(String narration, Date startsOn, Date endsOn, Amount amount, Account debitor, Account creditor) {
        return plan(narration, startsOn, endsOn, amount, debitor, creditor, ExecutionOfPlannedTransaction.SINGLE);
    }

    private PlannedTransaction plan(String narration, Date startsOn, Date endsOn, Amount amount, Account debitor, Account creditor, ExecutionOfPlannedTransaction executionOfPlannedTransaction) {
        ledger.assertThatAccountExists(debitor);
        ledger.assertThatAccountExists(creditor);

        PlannedTransaction newTransaction = new PlannedTransaction(narration, amount, debitor, creditor, startsOn, endsOn, executionOfPlannedTransaction);
        plannedTransactions.add(newTransaction);
        return newTransaction;
    }

    public void remove(PlannedTransaction transaction) {
        plannedTransactions.remove(transaction);
    }


}
