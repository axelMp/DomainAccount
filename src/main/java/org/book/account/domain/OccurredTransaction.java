package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import java.util.Date;

public class OccurredTransaction extends Transaction {

    OccurredTransaction(String description,Amount amount,Account from,Account to,Date occurredOn) {
        super(description,false,amount,from,to);

        Validate.notNull(from, "The occurredOn must not be %s", null);
        this.occurredOn = occurredOn;
    }

    @Override
    public Amount valueAt(Date date) {
        Validate.notNull(date, "The date must not be %s", null);

        if ( date.after(occurredOn)) {
            return getAmount();
        } else {
            return Amount.noAmount();
        }
    }

    private final Date occurredOn;
}
