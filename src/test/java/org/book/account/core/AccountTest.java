package org.book.account.core;

import org.book.account.Utilities;
import org.book.account.domain.AccountType;
import org.book.account.domain.Amount;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class AccountTest {
    @Test
    public void noAssociatedTransactions_ClosedWithEmptyAmount() {
        Ledger ledger = new Ledger("randomName");
        final String anAccountName = "anAccountName";

        Account anAccount = ledger.createAccount(anAccountName, AccountType.ASSET);

        Assert.assertEquals(anAccount.closure(new Date()), Amount.noAmount());
    }

    @Test
    public void noAssociatedTransactions_ClosedWithEmptyAmountRelativeToOtherAccount() {
        Ledger ledger = new Ledger("randomName");
        final String anAccountName = "anAccountName";

        Account anAccount = ledger.createAccount(anAccountName, AccountType.ASSET);

        Assert.assertEquals(anAccount.closure(new Date(), CoreUtilities.generateRandomAccount(ledger)), Amount.noAmount());
    }

    @Test
    public void oneCreditedTransactionOnDateOfClosure_ClosedWithNegativeAmountRelativeToOtherAccount() {
        Ledger ledger = new Ledger("randomName");
        final String anAccountName = "anAccountName";
        final String anotherAccountName = "anotherAccountName";
        Date randomDay = new Date();
        int randomCents = 3849;
        Amount randomAmount = new Amount(randomCents, Amount.Currency.EUR);
        Amount negativeRandomAmount = new Amount(-randomCents, Amount.Currency.EUR);
        Account anAccount = ledger.createAccount(anAccountName, AccountType.INCOME);
        Account anotherAccount = ledger.createAccount(anotherAccountName, AccountType.INCOME);

        ledger.book("aNarration", randomDay, randomAmount, anAccount, anotherAccount);
        Assert.assertEquals(anAccount.closure(randomDay, anotherAccount), negativeRandomAmount);
    }

    @Test
    public void oneCreditedTransactionToRandomAccountOnDateOfClosure_ClosedNoAmountRelativeToOtherAccount() {
        Ledger ledger = new Ledger("randomName");
        final String anAccountName = "anAccountName";
        final String anotherAccountName = "anotherAccountName";
        Date randomDay = new Date();
        int randomCents = 3849;
        Amount randomAmount = new Amount(randomCents, Amount.Currency.EUR);
        Account anAccount = ledger.createAccount(anAccountName, AccountType.INCOME);
        Account anotherAccount = ledger.createAccount(anotherAccountName, AccountType.INCOME);

        ledger.book("aNarration", randomDay, randomAmount, anAccount, CoreUtilities.generateRandomAccount(ledger));
        Assert.assertEquals(anAccount.closure(randomDay, anotherAccount), Amount.noAmount());
    }

    @Test
    public void oneCreditedTransactionAfterDateOfClosure_ClosedWithNoAmountRelativeToOtherAccount() {
        Ledger ledger = new Ledger("randomName");
        final String anAccountName = "anAccountName";
        final String anotherAccountName = "anotherAccountName";
        Date randomDay = new Date();
        int randomCents = 3849;
        Amount randomAmount = new Amount(randomCents, Amount.Currency.EUR);
        Account anAccount = ledger.createAccount(anAccountName, AccountType.INCOME);
        Account anotherAccount = ledger.createAccount(anotherAccountName, AccountType.INCOME);

        ledger.book("aNarration", randomDay, randomAmount, anAccount, anotherAccount);
        Assert.assertEquals(anAccount.closure(Utilities.previousDay(randomDay), anotherAccount), Amount.noAmount());
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
        Account anAccount = ledger.createAccount(anAccountName, AccountType.INCOME);
        Account anotherAccount = ledger.createAccount(anotherAccountName, AccountType.INCOME);

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
        Account anAccount = ledger.createAccount(anAccountName, AccountType.INCOME);
        Account anotherAccount = ledger.createAccount(anotherAccountName, AccountType.INCOME);

        ledger.book("aNarration", randomDay, randomAmount, anAccount, anotherAccount);
        Assert.assertEquals(anAccount.closure(Utilities.previousDay(randomDay)), Amount.noAmount());
    }

    @Test
    public void oneCreditedTransactionBeforeDateOfClosure_ClosedWithNegativeAmount() {
        Ledger ledger = new Ledger("randomName");
        final String anAccountName = "anAccountName";
        final String anotherAccountName = "anotherAccountName";
        Date randomDay = new Date();
        Date dayOfTransaction = Utilities.previousDay(randomDay);
        Date dayOfClosure = randomDay;
        int randomCents = 3849;
        Amount randomAmount = new Amount(randomCents, Amount.Currency.EUR);
        Amount negativeRandomAmount = new Amount(-randomCents, Amount.Currency.EUR);
        Account anAccount = ledger.createAccount(anAccountName, AccountType.INCOME);
        Account anotherAccount = ledger.createAccount(anotherAccountName, AccountType.INCOME);

        ledger.book("aNarration", dayOfTransaction, randomAmount, anAccount, anotherAccount);
        Assert.assertEquals(anAccount.closure(dayOfClosure), negativeRandomAmount);
    }
}
