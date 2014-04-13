package org.book.account.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "account")
public class Account {
    private String name;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @ManyToOne
    @JoinColumn(name = "LEDGER_ID")
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
        for (Transaction aTransaction : ledger.getTransactions()) {
            if (aTransaction.getOccurredOn().before(date)) {
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

    public enum AccountType {
        INCOME,
        EXPENSE,
        SHORT_TERM_LIABILITY,
        LONG_TERM_LIABILITY,
        SHORT_TERM_ASSET,
        LONG_TERM_ASSET
    }
}
