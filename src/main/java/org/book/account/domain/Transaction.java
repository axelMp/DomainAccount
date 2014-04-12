package org.book.account.domain;

import java.util.Date;
import org.apache.commons.lang3.Validate;

public abstract class Transaction {
    Transaction(String description,boolean isPlanned,Amount amount,Account from,Account to) {
        Validate.notNull(from, "The from account must not be %s", null);
        Validate.notNull(to, "The to account must not be %s", null);

        setDescription(description);
        this.isPlanned = isPlanned;
        setAmount(amount);
        this.from = from;
        this.to = to;

    }

    public abstract Amount valueAt(Date date);

    public String getDescription() {
        return description;
    }

    boolean isPlanned() {
        return isPlanned;
    }

    public final void setDescription(String description) {
        Validate.notNull(description, "The description must not be %s", null);
        this.description = description;
    }

    protected Amount getAmount() {
        return amount;
    }

    public final void setAmount(Amount amount) {
        Validate.notNull(amount, "The amount must not be %s", null);
        this.amount = amount;
    }

    public Account getFrom() {
        return from;
    }

    public Account getTo() {
        return to;
    }

    private String description;
    private final boolean isPlanned;
    private Amount amount;
    private final Account from;
    private final Account to;
}
