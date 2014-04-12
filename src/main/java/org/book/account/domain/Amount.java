package org.book.account.domain;

/**
 * Value class of an amount of money
 */
public class Amount {
    public enum Currency {
        EUR,
        USD
    }

    public static Amount noAmount() {
        return new Amount(0,Currency.EUR);
    }

    public static Amount add(Amount anAmount,Amount anotherAmount ) {
        if ( ! anAmount.getCurrency().equals(anotherAmount.getCurrency()))   {
            throw new IllegalArgumentException("cannot sum amounts of different currencies ("+anAmount.getCurrency()+" and "+anotherAmount.getCurrency()+")");
        }
        return new Amount(anAmount.getCents()+anotherAmount.getCents(),anAmount.getCurrency());
    }

    public static Amount subtract(Amount anAmount, Amount anotherAmount) {
        if ( ! anAmount.getCurrency().equals(anotherAmount.getCurrency()))   {
            throw new IllegalArgumentException("cannot sum amounts of different currencies ("+anAmount.getCurrency()+" and "+anotherAmount.getCurrency()+")");
        }
        return new Amount(anAmount.getCents()-anotherAmount.getCents(),anAmount.getCurrency());
    }

    public Amount(Integer cents, Currency currency) {
        this.cents = cents;
        this.currency = currency;
    }

    private final Integer cents;
    private final Currency currency;

    public Integer getCents() {
        return cents;
    }

    public Currency getCurrency() {
        return currency;
    }
}
