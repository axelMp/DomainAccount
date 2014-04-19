package org.book.account.domain;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class ForecastServiceTest {
    @Test
    public void borderCase_noAssociatedTransactions_forecastsEmptyAmount() {
        Ledger ledger = new Ledger("randomName");
        final String anAccountName = "anAccountName";
        Account anAccount = ledger.createAccount(anAccountName, Account.AccountType.ASSET);
        Date dateOfClosure = new Date();
        ForecastService sut = new ForecastService();
        Budget plan = new Budget(ledger);

        Assert.assertEquals(sut.forecastClosure(ledger, anAccount, plan, dateOfClosure), Amount.noAmount());
    }

    @Test
    public void noAssociatedPlannedTransactions_forecastsIdenticalToClosure() {
        Ledger ledger = new Ledger("randomName");
        final String anAccountName = "anAccountName";
        Account anAccount = ledger.createAccount(anAccountName, Account.AccountType.ASSET);
        Date forecastOn = new Date();
        ForecastService sut = new ForecastService();
        Budget plan = new Budget(ledger);
        Account anotherAccount = ledger.createAccount("anotherAccountName", Account.AccountType.INCOME);
        Amount randomAmount = new Amount(23, Amount.Currency.EUR);
        ledger.book("aNarration", forecastOn, randomAmount, anAccount, anotherAccount);

        Assert.assertEquals(sut.forecastClosure(ledger, anAccount, plan, forecastOn), anAccount.closure(forecastOn));

    }
}
