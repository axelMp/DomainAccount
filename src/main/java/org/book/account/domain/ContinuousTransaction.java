package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import java.util.Date;

public class ContinuousTransaction extends Transaction {

    private final Date startsOn;
    private final Date endsOn;

    ContinuousTransaction(String description, Amount amount, Account from, Account to, Date startsOn, Date endsOn) {
        super(description, true, amount, from, to);
        Validate.notNull(startsOn, "The startsOn must not be null");
        Validate.notNull(endsOn, "The endsOn must not be null");

        if (startsOn.after(endsOn)) {
            throw new IllegalArgumentException("continuous transaction should have a start date before the given end date");
        }

        this.startsOn = startsOn;
        this.endsOn = endsOn;
    }

    @Override
    public Amount valueAt(Date date) {
        Validate.notNull(date, "The date must not be null");

        if (date.after(endsOn)) {
            return getAmount();
        } else if (date.before(startsOn)) {
            return Amount.noAmount();
        } else if (startsOn.getTime() == endsOn.getTime()) {
            return getAmount();
        } else {
            double durationTransaction = endsOn.getTime() - startsOn.getTime();
            double durationTillDate = date.getTime() - startsOn.getTime();
            double percentage = durationTillDate / durationTransaction;
            Integer partialAmount = (int) Math.round(percentage * getAmount().getCents());
            return new Amount(partialAmount, getAmount().getCurrency());
        }
    }
}
