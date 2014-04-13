package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class PlannedTransaction extends Transaction {
    private final Date startsOn;
    private final Date endsOn;
    private final boolean isContinuous;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    PlannedTransaction(String description, Amount amount, Account from, Account to, Date startsOn, Date endsOn, boolean isContinuous) {
        super(description, amount, from, to);

        Validate.notNull(startsOn, "The startsOn date must not be null");
        Validate.notNull(endsOn, "The endsOn on must not be null");
        if (startsOn.after(endsOn)) {
            throw new IllegalArgumentException("planned transaction should have a start date before the given end date");
        }

        this.isContinuous = isContinuous;
        this.startsOn = startsOn;
        this.endsOn = endsOn;
    }

    @Override
    public Amount valueAt(Date date) {
        Validate.notNull(date, "The date must not be null");

        if (isContinuous) {
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
        } else {
            if (date.after(startsOn)) {
                return getAmount();
            } else {
                return Amount.noAmount();
            }
        }
    }
}
