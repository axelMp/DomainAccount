package org.book.account.domain;

public class DomainUtilities {
    public static Amount generateRandomAmountInEuro() {
        int randomInt = (int) Math.round(10000 * Math.random());
        return new Amount(randomInt, Amount.Currency.EUR);
    }
}
