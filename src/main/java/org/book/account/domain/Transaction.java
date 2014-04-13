package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import java.util.Date;

public abstract class Transaction {
    private final Account from;
    private final Account to;
    private String description;
    private Amount amount;

    Transaction(String description, Amount amount, Account from, Account to) {
        Validate.notNull(from, "The from account must not be null");
        Validate.notNull(to, "The to account must not be null");

        setDescription(description);
        setAmount(amount);
        this.from = from;
        this.to = to;

    }

    public abstract Amount valueAt(Date date);

    public String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        Validate.notNull(description, "The description must not be null");
        this.description = description;
    }

    protected Amount getAmount() {
        return amount;
    }

    public final void setAmount(Amount amount) {
        Validate.notNull(amount, "The amount must not be null");
        this.amount = amount;
    }

    public Account getFrom() {
        return from;
    }

    public Account getTo() {
        return to;
    }
}
