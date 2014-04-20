package org.book.account.core;

import org.apache.commons.lang3.Validate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "planned_transaction")
public class PlannedTransaction implements ITransaction {
    @ManyToOne
    @JoinColumn(name = "debitor_id")
    private Account debitor;
    @ManyToOne
    @JoinColumn(name = "creditor_id")
    private Account creditor;
    private Date startsOn;
    private Date endsOn;
    private String narration;
    private Amount amount;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "EXECUTION")
    @Enumerated(EnumType.STRING)
    private Execution execution;

    // required by hibernate
    public PlannedTransaction() {

    }

    PlannedTransaction(String narration, Amount amount, Account debitor, Account creditor, Date startsOn, Date endsOn, Execution execution) {
        Validate.notNull(debitor, "The debitor account must not be null");
        Validate.notNull(creditor, "The creditor account must not be null");

        setNarration(narration);
        setAmount(amount);
        this.debitor = debitor;
        this.creditor = creditor;

        Validate.notNull(startsOn, "The startsOn date must not be null");
        Validate.notNull(endsOn, "The endsOn on must not be null");
        if (startsOn.after(endsOn)) {
            throw new IllegalArgumentException("planned transaction should have a start date before the given end date");
        }

        this.execution = execution;
        this.startsOn = startsOn;
        this.endsOn = endsOn;
    }

    public Execution getExecution() {
        return execution;
    }

    public Date getStartsOn() {
        return startsOn;
    }

    public Date getEndsOn() {
        return endsOn;
    }

    public String getNarration() {
        return narration;
    }

    public final void setNarration(String narration) {
        Validate.notNull(narration, "The narration must not be null");
        this.narration = narration;
    }

    private Amount getAmount() {
        return amount;
    }

    public final void setAmount(Amount amount) {
        Validate.notNull(amount, "The amount must not be null");
        this.amount = amount;
    }

    public Account getDebitor() {
        return debitor;
    }

    public Account getCreditor() {
        return creditor;
    }

    private Amount forecastSingle(Date date) {
        if (date.after(startsOn)) {
            return getAmount();
        } else {
            return Amount.noAmount();
        }
    }

    private Amount forecastLinearlyProgressing(Date date) {
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

    public Amount forecast(Date date) {
        Validate.notNull(date, "The date must not be null");

        switch (execution) {
            case SINGLE:
                return forecastSingle(date);
            case LINEARLY_PROGRESSING:
                return forecastLinearlyProgressing(date);
            default:
                throw new IllegalArgumentException("cannot forecast for execution type " + execution.toString());
        }
    }

    public enum Execution {
        SINGLE,
        LINEARLY_PROGRESSING
    }
}
