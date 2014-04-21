package org.book.account;

import java.util.Calendar;
import java.util.Date;

public class Utilities {
    public static Date previousDay(Date aDate) {
        return moveDay(-1, aDate);
    }

    public static Date nextDay(Date aDate) {
        return moveDay(1, aDate);
    }

    public static Date moveDay(int daysToMove, Date aDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(aDate);
        cal.add(Calendar.DAY_OF_YEAR, daysToMove);
        return cal.getTime();
    }

    public static Date today() {
        return new Date();
    }
}
