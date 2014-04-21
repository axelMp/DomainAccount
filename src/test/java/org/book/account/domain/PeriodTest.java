package org.book.account.domain;


import org.book.account.Utilities;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class PeriodTest {
    @Test(expected = IllegalArgumentException.class)
    public void plannedToEndBeforeItStarted_IllegalArgumentException() {
        Date randomDate = Utilities.today();
        Date dayBeforeRandomDate = Utilities.previousDay(randomDate);

        new Period(randomDate, dayBeforeRandomDate);
    }

    @Test
    public void movesDateToStartOfDay() {
        Date randomDate = Utilities.today();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(randomDate);
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        Date sut = Period.startOfDay(randomDate);
        calendar.setTime(sut);

        Assert.assertEquals(day, calendar.get(Calendar.DAY_OF_YEAR));
        Assert.assertEquals(month, calendar.get(Calendar.MONTH));
        Assert.assertEquals(year, calendar.get(Calendar.YEAR));
        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(0, calendar.get(Calendar.SECOND));
        Assert.assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }

    @Test
    public void movesDateToEndOfDay() {
        Date randomDate = Utilities.today();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(randomDate);
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        Date sut = Period.endOfDay(randomDate);
        calendar.setTime(sut);

        Assert.assertEquals(day, calendar.get(Calendar.DAY_OF_YEAR));
        Assert.assertEquals(month, calendar.get(Calendar.MONTH));
        Assert.assertEquals(year, calendar.get(Calendar.YEAR));
        Assert.assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(59, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(59, calendar.get(Calendar.SECOND));
        Assert.assertEquals(999, calendar.get(Calendar.MILLISECOND));
    }

    @Test
    public void periodStartsBetweenTodayAndForecastAndEndsAfterForecast_overlaps() {
        Date startFirstPeriod = Utilities.moveDay(10, Utilities.today());
        Date startSecondPeriod = Utilities.moveDay(-4, startFirstPeriod);
        Date endSecondPeriod = Utilities.moveDay(5, startFirstPeriod);
        Period sut = new Period(startSecondPeriod, endSecondPeriod);
        Period forecast = new Period(new Date(), startFirstPeriod);

        Assert.assertTrue(sut.overlapsWith(forecast));
    }

    @Test
    public void singleDayPeriod_numberOfDaysEqualsOne() {
        Date today = Utilities.today();
        Period sut = new Period(today, today);

        Assert.assertEquals(1, sut.numberDays());
    }

    @Test
    public void threeDayPeriod_numberOfDaysEqualsThree() {
        Date today = Utilities.today();
        Date twoDaysEarlier = Utilities.moveDay(-2, today);
        Period sut = new Period(twoDaysEarlier, today);

        Assert.assertEquals(3, sut.numberDays());
    }
}
