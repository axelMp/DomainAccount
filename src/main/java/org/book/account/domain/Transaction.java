package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Transaction implements ITransaction {

    private Account debitor;
    private Account creditor;
    private Date occurredOn;
    private String description;
    private Amount amount;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    Transaction(String narration, Amount amount, Account debitor, Account creditor, Date occurredOn) {
        Validate.notNull(debitor, "The debitor account must not be null");
        Validate.notNull(creditor, "The creditor account must not be null");
        Validate.notNull(occurredOn, "The occurredOn must not be null");

        setNarration(narration);
        setAmount(amount);
        this.debitor = debitor;
        this.creditor = creditor;
        this.occurredOn = occurredOn;
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
