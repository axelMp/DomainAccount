package org.book.account.domain;

import java.util.Date;

public class OccurredTransaction extends Transaction {

    OccurredTransaction(String description,Amount amount,Account from,Account to,Date occurredOn) {
        super(description,false,amount,from,to);
        if ( null == occurredOn) {
            throw new IllegalArgumentException("occurredOn has to be non-null");
        }
        this.occurredOn = occurredOn;
    }

    @Override
    public Amount valueAt(Date date) {
        if ( null == date) {
            throw new IllegalArgumentException("date has to be non-null");
        }

        if ( date.after(occurredOn)) {
            return getAmount();
        } else {
            return Amount.noAmount();
        }
    }

    private final Date occurredOn;
}
