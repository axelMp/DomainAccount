package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import java.util.Date;

public class NonRecurringTransaction extends Transaction {

    private final Date startsOn;
    private final Date endsOn;

    NonRecurringTransaction(String description, Amount amount, Account from, Account to, Date startsOn, Date endsOn) {
        super(description, true, amount, from, to);

        Validate.notNull(startsOn, "The startsOn date must not be null");
        Validate.notNull(endsOn, "The endsOn on must not be null");
        if (startsOn.after(endsOn)) {
            throw new IllegalArgumentException("non recurring transaction should have a start date before the given end date");
        }

        this.startsOn = startsOn;
        this.endsOn = endsOn;
    }

    @Override
    public Amount valueAt(Date date) {
        Validate.notNull(date, "The date must not be null");

        if (date.after(startsOn)) {
            return getAmount();
        } else {
            return Amount.noAmount();
        }
    }
}
