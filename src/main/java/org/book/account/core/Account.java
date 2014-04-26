package org.book.account.core;

import org.apache.commons.lang3.Validate;
import org.book.account.domain.*;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "account")
class Account implements IAccount {
    private String name;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    @ManyToOne
    @JoinColumn(name = "ledger_id")
    private Ledger ledger;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // required by hibernate
    Account() {

    }

    Account(String name, AccountType accountType, Ledger ledger) {
        this.name = name;
        this.accountType = accountType;
        this.ledger = ledger;
    }

    public Ledger getLedger() {
        return ledger;
    }

    public List<IPlannedTransaction> getPlannedTransactions() {
        List<IPlannedTransaction> plannedTransactions = new LinkedList<IPlannedTransaction>();
        for (IPlannedTransaction plannedTransaction : getLedger().getBudget().getPlannedTransactions()) {
            if (this.equals(plannedTransaction.getCreditor()) || this.equals(plannedTransaction.getDebitor())) {
                plannedTransactions.add(plannedTransaction);
            }
        }

        return plannedTransactions;
    }

    private Amount sum(ITransaction aTransaction, Amount currentSum) {
        if (aTransaction.getDebitor().equals(this)) {
            return Amount.subtract(currentSum, aTransaction.getAmount());
        } else {
            return Amount.add(currentSum, aTransaction.getAmount());
        }
    }

    public Amount closure(Date date, IAccount relativeTo) {
        Validate.notNull(date, "date cannot be null");
        Validate.notNull(relativeTo, "relativeTo cannot be null");

        Amount result = Amount.noAmount();
        for (ITransaction aTransaction : getTransactions()) {
            if (aTransaction.getCreditor().equals(relativeTo) || aTransaction.getDebitor().equals(relativeTo)) {
                if (aTransaction.getOccurredOn().before(date) || aTransaction.getOccurredOn().equals(date)) {
                    result = sum(aTransaction, result);
                }
            }
        }
        return result;
    }

    public Amount closure(Date date) {
        Validate.notNull(date, "date cannot be null");
        Amount result = Amount.noAmount();
        for (ITransaction aTransaction : getTransactions()) {
            if (aTransaction.getOccurredOn().before(date) || aTransaction.getOccurredOn().equals(date)) {
                result = sum(aTransaction, result);
            }
        }
        return result;
    }

    public List<ITransaction> getTransactions() {
        List<ITransaction> result = new LinkedList<ITransaction>();
        for (ITransaction transaction : ledger.getTransactions()) {
            if (transaction.getCreditor().equals(this) || transaction.getDebitor().equals(this)) {
                result.add(transaction);
            }
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public AccountType getAccountType() {
        return accountType;
    }
}
