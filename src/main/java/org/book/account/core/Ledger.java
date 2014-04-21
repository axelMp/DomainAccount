package org.book.account.core;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.book.account.domain.*;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

// public as it's an aggregate root
@Entity
@Table(name = "ledger")
public class Ledger implements ILedger {

    private static final Logger logger = LogManager.getLogger(Ledger.class.getName());
    @Column(name = "NAME")
    private String name;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "ledger_transactions",
            joinColumns = @JoinColumn(name = "id")
    )
    private List<Transaction> transactions = new LinkedList<Transaction>();
    @OneToMany(mappedBy = "ledger", cascade = CascadeType.ALL)
    private List<Account> accounts = new LinkedList<Account>();
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    private Budget budget;

    // required by hibernate
    Ledger() {

    }

    public Ledger(String name) {
        Validate.notNull(name, "The name must not be null");
        this.budget = new Budget(this);
        this.name = name;
    }

    public Budget getBudget() {
        return budget;
    }

    public List<IAccount> getAccounts() {
        return new LinkedList<IAccount>(accounts);
    }

    public List<ITransaction> getTransactions() {
        return new LinkedList<ITransaction>(transactions);
    }

    public String getName() {
        return name;
    }

    public Account createAccount(String name, AccountType accountType) {
        for (Account anAccount : accounts) {
            if (anAccount.getName().equals(name)) {
                throw new IllegalArgumentException("account with name " + name + " already exists");
            }
        }
        Account newAccount = new Account(name, accountType, this);
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

    public ITransaction book(String narration, Date occurredOn, Amount amount, IAccount debitor, IAccount creditor) {
        assertThatAccountExists((Account) debitor);
        assertThatAccountExists((Account) creditor);

        Transaction newTransaction = new Transaction(narration, amount, (Account) debitor, (Account) creditor, occurredOn);
        transactions.add(newTransaction);
        return newTransaction;
    }

    public void remove(ITransaction transaction) {
        transactions.remove((Transaction) transaction);
    }
}
