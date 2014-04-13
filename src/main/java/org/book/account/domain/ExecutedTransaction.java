package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import java.util.Date;

public class ExecutedTransaction extends Transaction {

    private final Date occurredOn;

    ExecutedTransaction(String description, Amount amount, Account from, Account to, Date occurredOn) {
        super(description, amount, from, to);

        Validate.notNull(from, "The occurredOn must not be null");
        this.occurredOn = occurredOn;
    }

    @Override
    public Amount valueAt(Date date) {
        Validate.notNull(date, "The date must not be null");

        if (date.after(occurredOn)) {
            return getAmount();
        } else {
            return Amount.noAmount();
        }
    }
}
