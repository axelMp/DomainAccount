package org.book.account.domain;

import java.util.Date;

public class NonRecurringTransaction extends Transaction {

    NonRecurringTransaction(String description,Amount amount,Account from,Account to,Date startsOn,Date endsOn) {
        super(description,true,amount,from,to);
        if ( null == startsOn) {
            throw new IllegalArgumentException("startsOn has to be non-null");
        }
        this.startsOn = startsOn;

        if ( null == endsOn) {
            throw new IllegalArgumentException("endsOn has to be non-null");
        }
        this.endsOn = endsOn;

        if ( startsOn.after(endsOn)) {
            throw new IllegalArgumentException("non recurring transaction should have a start date before the given end date");
        }
    }

    @Override
    public Amount valueAt(Date date) {
        if ( null == date) {
            throw new IllegalArgumentException("date has to be non-null");
        }

        if ( date.after(startsOn)) {
            return getAmount();
        } else {
            return Amount.noAmount();
        }
    }

    private final Date startsOn;
    private final Date endsOn;
}
