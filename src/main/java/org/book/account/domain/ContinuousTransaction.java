package org.book.account.domain;

import java.util.Date;

public class ContinuousTransaction extends Transaction {

    ContinuousTransaction(String description,Amount amount,Account from,Account to,Date startsOn,Date endsOn) {
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
            throw new IllegalArgumentException("continuous transaction should have a start date before the given end date");
        }
    }

    @Override
    public Amount valueAt(Date date) {
        if ( null == date) {
            throw new IllegalArgumentException("date has to be non-null");
        }

        if ( date.after(endsOn)) {
            return getAmount();
        } else if ( date.before(startsOn)){
            return Amount.noAmount();
        } else if ( startsOn.getTime() == endsOn.getTime() ) {
            return getAmount();
        } else {
            double durationTransaction = endsOn.getTime() - startsOn.getTime();
            double durationTillDate    = date.getTime() - startsOn.getTime();
            double percentage = durationTillDate / durationTransaction;
            Integer partialAmount = (int)Math.round(percentage*getAmount().getCents());
            return new Amount(partialAmount,getAmount().getCurrency());
        }
    }

    private final Date startsOn;
    private final Date endsOn;
}
