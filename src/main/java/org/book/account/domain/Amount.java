package org.book.account.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Value class of an amount of money
 */
@Embeddable
public class Amount {
    @Column(name = "CENTS")
    private Integer cents;
    @Column(name = "CURRENCY")
    @Enumerated(EnumType.STRING)
    private Currency currency;

    // required by hibernate
    Amount() {
    }

    public Amount(Integer cents, Currency currency) {
        this.cents = cents;
        this.currency = currency;
    }

    public static Amount noAmount() {
        return new Amount(0, Currency.EUR);
    }

    public static Amount add(Amount anAmount, Amount anotherAmount) {
        if (!anAmount.getCurrency().equals(anotherAmount.getCurrency())) {
            throw new IllegalArgumentException("cannot sum amounts of different currencies (" + anAmount.getCurrency() + " and " + anotherAmount.getCurrency() + ")");
        }
        return new Amount(anAmount.getCents() + anotherAmount.getCents(), anAmount.getCurrency());
    }

    public static Amount subtract(Amount anAmount, Amount anotherAmount) {
        if (!anAmount.getCurrency().equals(anotherAmount.getCurrency())) {
            throw new IllegalArgumentException("cannot sum amounts of different currencies (" + anAmount.getCurrency() + " and " + anotherAmount.getCurrency() + ")");
        }
        return new Amount(anAmount.getCents() - anotherAmount.getCents(), anAmount.getCurrency());
    }

    public Integer getCents() {
        return cents;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }

        if (!(that instanceof Amount)) {
            return false;
        }

        return getCurrency().equals(((Amount) that).getCurrency()) &&
                getCents().equals(((Amount) that).getCents());
    }

    @Override
    public int hashCode() {
        return getCents();
    }

    @Override
    public String toString() {
        return getCents().toString() + getCurrency().toString();
    }

    public enum Currency {
        EUR,
        USD
    }
}
