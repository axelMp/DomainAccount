package org.book.account.domain;

import java.util.Calendar;
import java.util.Date;

public class DateUtilities {
    static Date previousDay(Date aDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(aDate);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return cal.getTime();
    }
}
