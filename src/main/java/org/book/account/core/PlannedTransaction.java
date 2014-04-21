package org.book.account.core;

import org.apache.commons.lang3.Validate;
import org.book.account.domain.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "planned_transaction")
class PlannedTransaction implements IPlannedTransaction {
    @ManyToOne
    @JoinColumn(name = "debitor_id")
    private Account debitor;
    @ManyToOne
    @JoinColumn(name = "creditor_id")
    private Account creditor;
    private Schedule schedule;
    private String narration;
    private Amount amount;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // required by hibernate
    PlannedTransaction() {
    }


    PlannedTransaction(String narration, Amount amount, Account debitor, Account creditor, Schedule schedule) {
        Validate.notNull(debitor, "The debitor account must not be null");
        Validate.notNull(creditor, "The creditor account must not be null");
        Validate.notNull(schedule, "The schedule must not be null");

        setNarration(narration);
        setAmount(amount);
        this.debitor = debitor;
        this.creditor = creditor;
        this.schedule = schedule;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        Validate.notNull(narration, "The narration must not be null");
        this.narration = narration;
    }

    private Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        Validate.notNull(amount, "The amount must not be null");
        this.amount = amount;
    }

    public Account getDebitor() {
        return debitor;
    }

    public Account getCreditor() {
        return creditor;
    }

    public boolean matchesAnyPerformedTransaction(List<ITransaction> transactions) {
        if (getSchedule().mayMatchWithIndividualTransaction()) {
            for (ITransaction transaction : transactions) {
                if (getSchedule().includes(transaction.getOccurredOn()) && matches(transaction)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matches(ITransaction transaction) {
        return transaction.getNarration().equals(getNarration());
    }

    private Amount forecastLinearlyProgressing(Date date) {
        if (date.after(getSchedule().getPeriod().getEndsOn())) {
            return getAmount();
        } else if (date.before(getSchedule().getPeriod().getStartsOn())) {
            return Amount.noAmount();
        } else if (getSchedule().getPeriod().getStartsOn().getTime() == getSchedule().getPeriod().getEndsOn().getTime()) {
            return getAmount();
        } else {
            double durationTransaction = getSchedule().getPeriod().getEndsOn().getTime() - getSchedule().getPeriod().getStartsOn().getTime();
            double durationTillDate = date.getTime() - getSchedule().getPeriod().getStartsOn().getTime();
            double percentage = durationTillDate / durationTransaction;
            Integer partialAmount = (int) Math.round(percentage * getAmount().getCents());
            return new Amount(partialAmount, getAmount().getCurrency());
        }
    }

    private Amount forecastLinearlyProgressing(Period forecastPeriod) {
        return Amount.subtract(forecastLinearlyProgressing(forecastPeriod.getEndsOn()), forecastLinearlyProgressing(forecastPeriod.getStartsOn()));
    }

    public Amount forecast(Period aPeriod) {
        Validate.notNull(aPeriod, "The period must not be null");
        if (!getSchedule().overlapsWith(aPeriod)) {
            return Amount.noAmount();
        }

        switch (getSchedule().getExecutionPolicy()) {
            case SINGLE:
                double percentage = getSchedule().percentageOfScheduleTookPlace(aPeriod);
                Integer partialAmount = (int) Math.round(percentage * getAmount().getCents());
                return new Amount(partialAmount, getAmount().getCurrency());
            case LINEARLY_PROGRESSING:
                return forecastLinearlyProgressing(aPeriod);
            default:
                throw new IllegalArgumentException("cannot forecast for executionOfPlannedTransaction type " + getSchedule().getExecutionPolicy().toString());
        }
    }
}
