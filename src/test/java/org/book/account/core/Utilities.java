package org.book.account.core;

import org.book.account.domain.AccountType;
import org.book.account.domain.Amount;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Utilities {
    static Date previousDay(Date aDate) {
        return moveDay(-1, aDate);
    }

    static Date nextDay(Date aDate) {
        return moveDay(1, aDate);
    }

    static Date moveDay(int daysToMove, Date aDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(aDate);
        cal.add(Calendar.DAY_OF_YEAR, daysToMove);
        return cal.getTime();
    }

    static Date today() {
        return new Date();
    }

    static Amount generateRandomAmountInEuro() {
        int randomInt = (int) Math.round(10000 * Math.random());
        return new Amount(randomInt, Amount.Currency.EUR);
    }

    static Account generateRandomAccount(Ledger aLedger) {
        final String anAccountName = UUID.randomUUID().toString();
        int randomTypeIndex = (int) Math.round(4 * Math.random());
        AccountType aType;
        switch (randomTypeIndex) {
            case 0:
                aType = AccountType.ASSET;
                break;
            case 1:
                aType = AccountType.EXPENSE;
                break;
            case 2:
                aType = AccountType.LIABILITY;
                break;
            default:
                aType = AccountType.INCOME;
        }
        return aLedger.createAccount(anAccountName, aType);
    }
}