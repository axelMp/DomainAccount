package org.book.account.domain;


import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class PeriodTest {
    @Test(expected = IllegalArgumentException.class)
    public void plannedToEndBeforeItStarted_IllegalArgumentException() {
        Date randomDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(randomDate);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date dayBeforeRandomDate = cal.getTime();

        new Period(randomDate, dayBeforeRandomDate);
    }
}
