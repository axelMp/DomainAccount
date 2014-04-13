package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
public class AccountSystem {
    @OneToMany
    private final List<PlannedTransaction> plannedTransactions = new LinkedList<PlannedTransaction>();
    @OneToMany
    private final List<ExecutedTransaction> executedTransactions = new LinkedList<ExecutedTransaction>();
    @Column(name = "HIERARCHY")
    private final AccountHierarchy accounts = new AccountHierarchy();

    private final List<Indicator> indicators = new LinkedList<Indicator>();
    @Column(name = "NAME")
    private final String name;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public AccountSystem(String name) {
        Validate.notNull(name, "The name must not be null");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Account generateAccount(String name, Account.AccountType accountType) {
        Account newAccount = new Account(name, accountType);
        accounts.add(newAccount);
        return newAccount;
    }

    public void remove(Account account) {
        accounts.remove(account);
    }

    public PhysicalAccount generatePhysicalAccount(String name) {
        PhysicalAccount newAccount = new PhysicalAccount(name);
        accounts.add(newAccount);
        return newAccount;
    }

    public void remove(PhysicalAccount account) {
        accounts.remove(account);
    }

    public PlannedTransaction plan(String text, Date startsOn, Date endsOn, Amount amount, Account from, Account to, boolean isContinuous) {
        accounts.assertThatAccountExists(from);
        accounts.assertThatAccountExists(to);

        PlannedTransaction newTransaction = new PlannedTransaction(text, amount, from, to, startsOn, endsOn, isContinuous);
        plannedTransactions.add(newTransaction);
        newTransaction.getFrom().add(newTransaction);
        newTransaction.getTo().add(newTransaction);
        return newTransaction;
    }

    public ExecutedTransaction book(String text, Date occurredOn, Amount amount, Account from, Account to) {
        accounts.assertThatAccountExists(from);
        accounts.assertThatAccountExists(to);

        ExecutedTransaction newTransaction = new ExecutedTransaction(text, amount, from, to, occurredOn);
        executedTransactions.add(newTransaction);
        newTransaction.getFrom().add(newTransaction);
        newTransaction.getTo().add(newTransaction);
        return newTransaction;
    }

    public void remove(ExecutedTransaction transaction) {
        executedTransactions.remove(transaction);
        transaction.getFrom().remove(transaction);
        transaction.getTo().remove(transaction);
    }

    public void remove(PlannedTransaction transaction) {
        plannedTransactions.remove(transaction);
        transaction.getFrom().remove(transaction);
        transaction.getTo().remove(transaction);
    }
    public void track(Indicator anIndicator) {
        Validate.notNull(anIndicator, "The indicator must not be null");

        indicators.add(anIndicator);
    }

    public void trackNoLonger(Indicator anIndicator) {
        indicators.remove(anIndicator);
    }

}
