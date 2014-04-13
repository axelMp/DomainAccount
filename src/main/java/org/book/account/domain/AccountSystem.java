package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class AccountSystem {
    private final List<Transaction> transactions = new LinkedList<Transaction>();
    private final AccountHierarchy accounts = new AccountHierarchy();
    private final List<Indicator> indicators = new LinkedList<Indicator>();
    private final String name;

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

    public Transaction plan(String text, Date startsOn, Date endsOn, Amount amount, Account from, Account to, boolean isContinuous) {
        accounts.assertThatAccountExists(from);
        accounts.assertThatAccountExists(to);

        Transaction newTransaction;

        if (isContinuous) {
            newTransaction = new ContinuousTransaction(text, amount, from, to, startsOn, endsOn);
        } else {
            newTransaction = new NonRecurringTransaction(text, amount, from, to, startsOn, endsOn);
        }
        add(newTransaction);
        return newTransaction;
    }

    public Transaction book(String text, Date occurredOn, Amount amount, Account from, Account to) {
        accounts.assertThatAccountExists(from);
        accounts.assertThatAccountExists(to);

        Transaction newTransaction = new OccurredTransaction(text, amount, from, to, occurredOn);
        add(newTransaction);
        return newTransaction;
    }

    public void remove(Transaction transaction) {
        transactions.remove(transaction);
        transaction.getFrom().remove(transaction);
        transaction.getTo().remove(transaction);
    }

    private void add(Transaction aTransaction) {
        transactions.add(aTransaction);
        aTransaction.getFrom().add(aTransaction);
        aTransaction.getTo().add(aTransaction);
    }

    public void track(Indicator anIndicator) {
        Validate.notNull(anIndicator, "The indicator must not be null");

        indicators.add(anIndicator);
    }

    public void trackNoLonger(Indicator anIndicator) {
        indicators.remove(anIndicator);
    }

}
