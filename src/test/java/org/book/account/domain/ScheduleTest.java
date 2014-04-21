package org.book.account.domain;

import org.book.account.Utilities;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;


public class ScheduleTest {

    @Test
    public void linearlyProgressingPolicy_periodStartsBetweenTodayAndForecastAndEndsAfterForecast_halfOverlap() {
        Date forecastOn = Utilities.moveDay(10, Utilities.today());
        Date planStartsOn = Utilities.moveDay(-4, forecastOn); // 5 common days
        Date planEndsOn = Utilities.moveDay(5, forecastOn); // 5 additional days after the planned period
        Period plan = new Period(planStartsOn, planEndsOn);
        Schedule sut = new Schedule(plan, ExecutionPolicy.LINEARLY_PROGRESSING);
        Period forecast = new Period(new Date(), forecastOn);
        double percentage = sut.percentageOfScheduleTookPlace(forecast);

        Assert.assertEquals(0.5, percentage, 0.001);
    }
}
