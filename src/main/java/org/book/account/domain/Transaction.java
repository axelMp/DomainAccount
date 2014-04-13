package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Transaction implements ITransaction {

    private final Account from;
    private final Account to;
    private final Date occurredOn;
    private String description;
    private Amount amount;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    Transaction(String description, Amount amount, Account from, Account to, Date occurredOn) {
        Validate.notNull(from, "The from account must not be null");
        Validate.notNull(to, "The to account must not be null");
        Validate.notNull(from, "The occurredOn must not be null");

        setDescription(description);
        setAmount(amount);
        this.from = from;
        this.to = to;
        this.occurredOn = occurredOn;
    }

    public String getNarration() {
        return description;
    }

    public void setDescription(String description) {
        Validate.notNull(description, "The description must not be null");
        this.description = description;
    }

    private Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        Validate.notNull(amount, "The amount must not be null");
        this.amount = amount;
    }

    public Account getDebitor() {
        return from;
    }

    public Account getCreditor() {
        return to;
    }

    public Amount valueAt(Date date) {
        Validate.notNull(date, "The date must not be null");

        if (date.after(occurredOn)) {
            return getAmount();
        } else {
            return Amount.noAmount();
        }
    }
}
