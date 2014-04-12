package org.book.account.domain;

import java.util.Date;

public abstract class Transaction {
    Transaction(String description,boolean isPlanned,Amount amount,Account from,Account to) {
        setDescription(description);
        this.isPlanned = isPlanned;
        setAmount(amount);

        if ( null == from ) {
            throw new IllegalArgumentException("from has to be non-null");
        }
        this.from = from;

        if ( null == to ) {
            throw new IllegalArgumentException("to has to be non-null");
        }
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
        if ( null == description ) {
            throw new IllegalArgumentException("description has to be non-null");
        }
        this.description = description;
    }

    protected Amount getAmount() {
        return amount;
    }

    public final void setAmount(Amount amount) {
        if ( null == amount ) {
            throw new IllegalArgumentException("amount has to be non-null");
        }
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
