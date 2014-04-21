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
    private Period period;
    private String narration;
    private Amount amount;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "EXECUTION")
    @Enumerated(EnumType.STRING)
    private ExecutionOfPlannedTransaction executionOfPlannedTransaction;

    // required by hibernate
    PlannedTransaction() {

    }

    PlannedTransaction(String narration, Amount amount, Account debitor, Account creditor, Date startsOn, Date endsOn, ExecutionOfPlannedTransaction executionOfPlannedTransaction) {
        Validate.notNull(debitor, "The debitor account must not be null");
        Validate.notNull(creditor, "The creditor account must not be null");

        setNarration(narration);
        setAmount(amount);
        this.debitor = debitor;
        this.creditor = creditor;
        this.period = new Period(startsOn, endsOn);
        this.executionOfPlannedTransaction = executionOfPlannedTransaction;
    }

    public Period getPeriod() {
        return period;
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
        switch (getExecutionOfPlannedTransaction()) {
            case SINGLE:
                for (ITransaction transaction : transactions) {
                    if (matchesSinglePlanned(transaction)) {
                        return true;
                    }
                }
                return false;
            case LINEARLY_PROGRESSING:
                return false;
            default:
                throw new IllegalArgumentException("cannot match transactions for executionOfPlannedTransaction type " + getExecutionOfPlannedTransaction().toString());
        }
    }

    public ExecutionOfPlannedTransaction getExecutionOfPlannedTransaction() {
        return executionOfPlannedTransaction;
    }

    private boolean matchesSinglePlanned(ITransaction transaction) {
        boolean identicalNarration = transaction.getNarration().equals(getNarration());
        return identicalNarration && getPeriod().includes(transaction.getOccurredOn());
    }

    private Amount forecastLinearlyProgressing(Date date) {
        if (date.after(getPeriod().getEndsOn())) {
            return getAmount();
        } else if (date.before(getPeriod().getStartsOn())) {
            return Amount.noAmount();
        } else if (getPeriod().getStartsOn().getTime() == getPeriod().getEndsOn().getTime()) {
            return getAmount();
        } else {
            double durationTransaction = period.getEndsOn().getTime() - getPeriod().getStartsOn().getTime();
            double durationTillDate = date.getTime() - getPeriod().getStartsOn().getTime();
            double percentage = durationTillDate / durationTransaction;
            Integer partialAmount = (int) Math.round(percentage * getAmount().getCents());
            return new Amount(partialAmount, getAmount().getCurrency());
        }
    }

    private Amount forecastLinearlyProgressing(Date from, Date until) {
        return Amount.subtract(forecastLinearlyProgressing(until), forecastLinearlyProgressing(from));
    }

    private Amount forecastSingle(Date from, Date until) {
        if (until.after(getPeriod().getStartsOn())) {
            return getAmount();
        } else {
            return Amount.noAmount();
        }
    }

    public Amount forecast(Date from, Date until) {
        Validate.notNull(from, "The from date must not be null");
        Validate.notNull(until, "The until date must not be null");
        Period forecastPeriod = new Period(from, until);
        if (!getPeriod().overlapsWith(forecastPeriod)) {
            return Amount.noAmount();
        }

        switch (getExecutionOfPlannedTransaction()) {
            case SINGLE:
                return forecastSingle(from, until);
            case LINEARLY_PROGRESSING:
                return forecastLinearlyProgressing(from, until);
            default:
                throw new IllegalArgumentException("cannot forecast for executionOfPlannedTransaction type " + getExecutionOfPlannedTransaction().toString());
        }
    }
}
