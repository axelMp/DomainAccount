package org.book.account.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Account {
    @Column(name = "NAME")
    private String name;
    @Column(name = "CURRENCY")
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "LEDGER")
    private Ledger ledger;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    Account(String name, AccountType accountType, Ledger ledger) {
        this.name = name;
        this.accountType = accountType;
        this.ledger = ledger;
    }

    public Amount closure(Date date) {
        Amount result = Amount.noAmount();
        for (Transaction aTransaction : ledger.getTransactions()) {
            if (aTransaction.getDebitor().equals(this)) {
                result = Amount.subtract(result, aTransaction.valueAt(date));
            } else {
                result = Amount.add(result, aTransaction.valueAt(date));
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

    public enum AccountType {
        INCOME,
        EXPENSE,
        SHORT_TERM_LIABILITY,
        LONG_TERM_LIABILITY,
        SHORT_TERM_ASSET,
        LONG_TERM_ASSET
    }
}
