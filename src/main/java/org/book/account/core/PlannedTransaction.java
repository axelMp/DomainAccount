package org.book.account.core;

import org.apache.commons.lang3.Validate;
import org.book.account.domain.*;

import javax.persistence.*;

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
        setNarration(narration);
        setAmount(amount);
        setDebitor(debitor);
        setCreditor(creditor);
        setSchedule(schedule);
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        Validate.notNull(schedule, "The schedule must not be null");
        this.schedule = schedule;
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

    public void setDebitor(IAccount debitor) {
        Validate.notNull(debitor, "The debitor account must not be null");
        this.debitor = (Account) debitor;
    }

    public Account getCreditor() {
        return creditor;
    }

    public void setCreditor(IAccount creditor) {
        Validate.notNull(creditor, "The creditor account must not be null");
        this.creditor = (Account) creditor;
    }

    public Long getId() {
        return id;
    }

    public Amount forecast(Period aPeriod) {
        Validate.notNull(aPeriod, "The period must not be null");
        if (!getSchedule().overlapsWith(aPeriod)) {
            return Amount.noAmount();
        }

        double percentage = getSchedule().percentageOfScheduleTookPlace(aPeriod);
        Integer partialAmount = (int) Math.round(percentage * getAmount().getCents());
        return new Amount(partialAmount, getAmount().getCurrency());
    }
}
