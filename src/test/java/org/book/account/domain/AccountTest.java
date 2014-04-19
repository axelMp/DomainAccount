package org.book.account.domain;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class AccountTest {
    @Test
    public void noAssociatedTransactions_ClosedWithEmptyAccount() {
        Ledger book = new Ledger("randomName");
        final String anAccountName = "anAccountName";
        Account anAccount = book.createAccount(anAccountName, Account.AccountType.ASSET);
        Assert.assertEquals(anAccount.closure(new Date()), Amount.noAmount());
    }
}
