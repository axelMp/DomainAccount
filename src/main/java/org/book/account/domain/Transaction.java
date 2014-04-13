package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transaction")
public class Transaction implements ITransaction {
    @ManyToOne
    @JoinColumn(name = "debitor_id")
    private Account debitor;
    @ManyToOne
    @JoinColumn(name = "creditor_id")
    private Account creditor;
    private Date occurredOn;
    private String description;
    private Amount amount;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Adding the join information to ledger results in massive duplication
    @ManyToOne
    @JoinColumn(name = "ledger_id")
    private Ledger ledger;

    // for hibernate
    public Transaction() {

    }

    Transaction(String narration, Amount amount, Account debitor, Account creditor, Date occurredOn, Ledger ledger) {
        Validate.notNull(debitor, "The debitor account must not be null");
        Validate.notNull(creditor, "The creditor account must not be null");
        Validate.notNull(occurredOn, "The occurredOn must not be null");

        setNarration(narration);
        setAmount(amount);
        this.debitor = debitor;
        this.creditor = creditor;
        this.occurredOn = occurredOn;
        this.ledger = ledger;
    }

    public Date getOccurredOn() {
        return occurredOn;
    }

    public String getNarration() {
        return description;
    }

    public void setNarration(String narration) {
        Validate.notNull(narration, "The narration must not be null");
        this.description = narration;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        Validate.notNull(amount, "The amount must not be null");
        this.amount = amount;
    }

    public Account getDebitor() {
        return debitor;
    }

    public Account getCreditor() {
        return creditor;
    }
}
