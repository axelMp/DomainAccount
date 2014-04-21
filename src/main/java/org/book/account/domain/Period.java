package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import javax.persistence.Embeddable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
Value type for a period of complete day(s).
 */
@Embeddable
public class Period {
    private Date startsOn;
    private Date endsOn;

    // required by hibernate
    Period() {
    }

    public Period(Date startsOn, Date endsOn) {
        Validate.notNull(startsOn, "The startsOn date must not be null");
        Validate.notNull(endsOn, "The endsOn on must not be null");
        if (startsOn.after(endsOn)) {
            throw new IllegalArgumentException("period should have a start date before the given end date");
        }

        this.startsOn = startOfDay(startsOn);
        this.endsOn = endOfDay(endsOn);
    }

    public static Date startOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar.getTime();
    }

    public static Date endOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 999);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        return calendar.getTime();
    }

    public static Period overlap(Period aPeriod, Period anotherPeriod) {
        Validate.notNull(aPeriod, "The anPeriod date must not be null");
        Validate.notNull(anotherPeriod, "The anotherPeriod on must not be null");
        Validate.isTrue(aPeriod.overlapsWith(anotherPeriod), "periods should overlap:" + aPeriod.toString() + " and " + anotherPeriod.toString());

        Date from = aPeriod.getStartsOn().after(anotherPeriod.getStartsOn()) ? aPeriod.getStartsOn() : anotherPeriod.getStartsOn();
        Date until = aPeriod.getEndsOn().before(anotherPeriod.getEndsOn()) ? aPeriod.getEndsOn() : anotherPeriod.getEndsOn();
        return new Period(from, until);
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

    public int numberDays() {
        // difference at end of day are whole days
        long millisecondsAtEndOfLastDay = getEndsOn().getTime();
        long millisecondsAtEndOfFirstDay = endOfDay(getStartsOn()).getTime();
        long millisecondsPerDay = 24 * 60 * 60 * 1000;
        // make up for missing first day
        return 1 + (int) ((millisecondsAtEndOfLastDay - millisecondsAtEndOfFirstDay) / millisecondsPerDay);
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(getStartsOn()) + " - " + sdf.format(getEndsOn());
    }
}
