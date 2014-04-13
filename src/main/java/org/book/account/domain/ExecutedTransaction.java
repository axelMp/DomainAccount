package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class ExecutedTransaction extends Transaction {
    private final Date occurredOn;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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
