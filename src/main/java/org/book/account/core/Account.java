package org.book.account.core;

import org.book.account.domain.AccountType;
import org.book.account.domain.Amount;
import org.book.account.domain.IAccount;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "account")
public class Account implements IAccount {
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
    public Account() {

    }

    Account(String name, AccountType accountType, Ledger ledger) {
        this.name = name;
        this.accountType = accountType;
        this.ledger = ledger;
    }

    public Amount closure(Date date) {
        Amount result = Amount.noAmount();
        for (Transaction aTransaction : ledger.getTransactions(this)) {
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

    public String getName() {
        return name;
    }

    public AccountType getAccountType() {
        return accountType;
    }


}
