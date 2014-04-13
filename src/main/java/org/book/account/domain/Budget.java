package org.book.account.domain;

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

    public PlannedTransaction plan(String text, Date startsOn, Date endsOn, Amount amount, Account from, Account to, boolean isContinuous) {
        // TODO assertThatAccountExists(from);
        // TODO assertThatAccountExists(to);

        PlannedTransaction newTransaction = new PlannedTransaction(text, amount, from, to, startsOn, endsOn, isContinuous);
        plannedTransactions.add(newTransaction);
        newTransaction.getDebitor().add(newTransaction);
        newTransaction.getCreditor().add(newTransaction);
        return newTransaction;
    }

    public void remove(PlannedTransaction transaction) {
        plannedTransactions.remove(transaction);
        transaction.getDebitor().remove(transaction);
        transaction.getCreditor().remove(transaction);
    }
}
