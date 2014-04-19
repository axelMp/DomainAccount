package org.book.account.domain;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class AccountTest {
    @Test
    public void noAssociatedTransactions_ClosedWithEmptyAmount() {
        Ledger ledger = new Ledger("randomName");
        final String anAccountName = "anAccountName";

        Account anAccount = ledger.createAccount(anAccountName, Account.AccountType.ASSET);

        Assert.assertEquals(anAccount.closure(new Date()), Amount.noAmount());
    }

    @Test
    public void oneCreditedTransactionOnDateOfClosure_ClosedWithNegativeAmount() {
        Ledger ledger = new Ledger("randomName");
        final String anAccountName = "anAccountName";
        final String anotherAccountName = "anotherAccountName";
        Date randomDay = new Date();
        int randomCents = 3849;
        Amount randomAmount = new Amount(randomCents, Amount.Currency.EUR);
        Amount negativeRandomAmount = new Amount(-randomCents, Amount.Currency.EUR);
        Account anAccount = ledger.createAccount(anAccountName, Account.AccountType.INCOME);
        Account anotherAccount = ledger.createAccount(anotherAccountName, Account.AccountType.INCOME);

        ledger.book("aNarration", randomDay, randomAmount, anAccount, anotherAccount);
        Assert.assertEquals(anAccount.closure(randomDay), negativeRandomAmount);
    }

    @Test
    public void oneCreditedTransactionAfterDateOfClosure_ClosedWithNoAmount() {
        Ledger ledger = new Ledger("randomName");
        final String anAccountName = "anAccountName";
        final String anotherAccountName = "anotherAccountName";
        Date randomDay = new Date();
        int randomCents = 3849;
        Amount randomAmount = new Amount(randomCents, Amount.Currency.EUR);
        Account anAccount = ledger.createAccount(anAccountName, Account.AccountType.INCOME);
        Account anotherAccount = ledger.createAccount(anotherAccountName, Account.AccountType.INCOME);

        ledger.book("aNarration", randomDay, randomAmount, anAccount, anotherAccount);
        Assert.assertEquals(anAccount.closure(DateUtilities.previousDay(randomDay)), Amount.noAmount());
    }

    @Test
    public void oneCreditedTransactionBeforeDateOfClosure_ClosedWithNegativeAmount() {
        Ledger ledger = new Ledger("randomName");
        final String anAccountName = "anAccountName";
        final String anotherAccountName = "anotherAccountName";
        Date randomDay = new Date();
        Date dayOfTransaction = DateUtilities.previousDay(randomDay);
        Date dayOfClosure = randomDay;
        int randomCents = 3849;
        Amount randomAmount = new Amount(randomCents, Amount.Currency.EUR);
        Amount negativeRandomAmount = new Amount(-randomCents, Amount.Currency.EUR);
        Account anAccount = ledger.createAccount(anAccountName, Account.AccountType.INCOME);
        Account anotherAccount = ledger.createAccount(anotherAccountName, Account.AccountType.INCOME);

        ledger.book("aNarration", dayOfTransaction, randomAmount, anAccount, anotherAccount);
        Assert.assertEquals(anAccount.closure(dayOfClosure), negativeRandomAmount);
    }


}
