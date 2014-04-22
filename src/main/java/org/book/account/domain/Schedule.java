package org.book.account.domain;


import org.apache.commons.lang3.Validate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

/**
 * Value class of how and when a planned transaction will take place
 */
@Embeddable
public class Schedule {
    private Period period;
    @Column(name = "EXECUTION")
    @Enumerated(EnumType.STRING)
    private ExecutionPolicy executionPolicy;

    // hibernate
    Schedule() {
    }

    public Schedule(Period period, ExecutionPolicy executionPolicy) {
        Validate.notNull(period, "period cannot be null");
        this.period = period;
        this.executionPolicy = executionPolicy;
    }

    public boolean includes(Date date) {
        return period.includes(date);
    }

    public boolean overlapsWith(Period period) {
        return period.overlapsWith(period);
    }

    public Period getPeriod() {
        return period;
    }

    public ExecutionPolicy getExecutionPolicy() {
        return executionPolicy;
    }

    public double percentageOfScheduleTookPlace(Period period) {
        switch (getExecutionPolicy()) {
            case SINGLE:
                return percentageOfSingleSchedule(period);
            case LINEARLY_PROGRESSING:
                return percentageOfLinearProgressingSchedule(period);
            default:
                throw new IllegalArgumentException("unhandled policy " + getExecutionPolicy().toString());
        }
    }

    private double percentageOfSingleSchedule(Period period) {
        if (period.overlapsWith(getPeriod())) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

    private double percentageOfLinearProgressingSchedule(Period period) {
        if (!period.overlapsWith(getPeriod())) {
            return 0.0;
        }

        return ((double) Period.overlap(period, getPeriod()).numberDays()) / ((double) getPeriod().numberDays());
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }

        if (!(that instanceof Schedule)) {
            return false;
        }

        return getPeriod().equals(((Schedule) that).getPeriod()) &&
                getExecutionPolicy().equals(((Schedule) that).getExecutionPolicy());
    }

    @Override
    public int hashCode() {
        int hashCodePeriod = getPeriod().hashCode();
        if (getExecutionPolicy().equals(ExecutionPolicy.LINEARLY_PROGRESSING)) {
            return hashCodePeriod + 1;
        } else {
            return hashCodePeriod;
        }
    }
}
