package org.book.account.domain;


import org.book.account.Utilities;
import org.junit.Test;

import java.util.Date;

public class PeriodTest {
    @Test(expected = IllegalArgumentException.class)
    public void plannedToEndBeforeItStarted_IllegalArgumentException() {
        Date randomDate = Utilities.today();
        Date dayBeforeRandomDate = Utilities.previousDay(randomDate);

        new Period(randomDate, dayBeforeRandomDate);
    }


}
