package org.book.account.domain;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Ledger {

    private static final Logger logger = LogManager.getLogger(Ledger.class.getName());
    @Column(name = "NAME")
    private final String name;
    @OneToMany
    private List<Transaction> transactions = new LinkedList<Transaction>();
    @OneToMany
    private List<Account> accounts = new LinkedList<Account>();
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Ledger(String name) {
        Validate.notNull(name, "The name must not be null");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Account createAccount(String name, Account.AccountType accountType) {
        for (Account anAccount : accounts) {
            if (anAccount.getName().equals(name)) {
                throw new IllegalArgumentException("account with name " + name + " already exists");
            }
        }
        Account newAccount = new Account(name, accountType);
        accounts.add(newAccount);
        return newAccount;
    }

    void assertThatAccountExists(Account anAccount) {
        if (!accounts.contains(anAccount)) {
            if (logger.isErrorEnabled()) {
                logger.error("account " + anAccount.getName() + " unknown");

                StringBuilder builder = new StringBuilder();
                for (Account account : accounts) {
                    builder.append(account.getName());
                    builder.append(" ");
                }
                logger.error("known accounts are " + builder.toString());
            }

            throw new IllegalArgumentException("Account " + anAccount.getName() + " does not exist");
        }
    }

    public void remove(Account account) {
        accounts.remove(account);
    }

    public Transaction book(String text, Date occurredOn, Amount amount, Account from, Account to) {
        assertThatAccountExists(from);
        assertThatAccountExists(to);

        Transaction newTransaction = new Transaction(text, amount, from, to, occurredOn);
        transactions.add(newTransaction);
        newTransaction.getDebitor().add(newTransaction);
        newTransaction.getCreditor().add(newTransaction);
        return newTransaction;
    }

    public void remove(Transaction transaction) {
        transactions.remove(transaction);
        transaction.getDebitor().remove(transaction);
        transaction.getCreditor().remove(transaction);
    }
}
