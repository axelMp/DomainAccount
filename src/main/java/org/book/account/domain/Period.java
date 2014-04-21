package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import javax.persistence.Embeddable;
import java.util.Date;

@Embeddable
public class Period {
    private Date startsOn;
    private Date endsOn;

    // required by hibernate
    Period() {
    }

    // TODO normalize on start and end of DAY
    public Period(Date startsOn, Date endsOn) {
        Validate.notNull(startsOn, "The startsOn date must not be null");
        Validate.notNull(endsOn, "The endsOn on must not be null");
        if (startsOn.after(endsOn)) {
            throw new IllegalArgumentException("planned transaction should have a start date before the given end date");
        }

        this.startsOn = startsOn;
        this.endsOn = endsOn;
    }

    public boolean includes(Date aDate) {
        boolean tookPlaceAfterPlannedStartsOn = !aDate.before(getStartsOn());
        boolean tookPlaceBeforePlannedEndsOn = !aDate.after(getEndsOn());
        return (tookPlaceAfterPlannedStartsOn && tookPlaceBeforePlannedEndsOn);
    }

    public boolean overlapsWith(Period other) {
        boolean planAlreadyOverdue = other.getStartsOn().after(getEndsOn());
        boolean planExpectedAfterForecast = getStartsOn().after(other.getEndsOn());
        return !(planAlreadyOverdue || planExpectedAfterForecast);
    }

    public Date getStartsOn() {
        return startsOn;
    }

    public Date getEndsOn() {
        return endsOn;
    }
}
