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

        Assert.assertEquals(sut.forecastClosure(ledger, anAccount, dateOfClosure), Amount.noAmount());
    }
}
