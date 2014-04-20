package org.book.account.core;

import org.apache.commons.lang3.Validate;
import org.book.account.domain.*;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "account")
public class Account implements IAccount {
    private String name;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @ManyToOne
    @JoinColumn(name = "ledger_id")
    private Ledger ledger;

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // required by hibernate
    public Account() {

    }

    Account(String name, AccountType accountType, Ledger ledger, Budget budget) {
        this.name = name;
        this.accountType = accountType;
        this.ledger = ledger;
        this.budget = budget;
    }

    public List<IPlannedTransaction> getPlannedTransactions() {
        List<IPlannedTransaction> plannedTransactions = new LinkedList<IPlannedTransaction>();
        for (IPlannedTransaction plannedTransaction : budget.getPlannedTransactions()) {
            if (this.equals(plannedTransaction.getCreditor()) || this.equals(plannedTransaction.getDebitor())) {
                plannedTransactions.add(plannedTransaction);
            }
        }

        return plannedTransactions;
    }

    public Amount forecast(Date forecastOn) {
        Validate.notNull(forecastOn, "forecastOn cannot be null");
        ForecastService forecastService = new ForecastService();
        return forecastService.forecastClosure(this, forecastOn);
    }

    public Amount closure(Date date) {
        Amount result = Amount.noAmount();
        for (ITransaction aTransaction : getTransactions()) {
            if (aTransaction.getOccurredOn().before(date) || aTransaction.getOccurredOn().equals(date)) {
                if (aTransaction.getDebitor().equals(this)) {
                    result = Amount.subtract(result, aTransaction.getAmount());
                } else {
                    result = Amount.add(result, aTransaction.getAmount());
                }
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
