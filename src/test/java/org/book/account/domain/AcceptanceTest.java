package org.book.account.domain;


import org.junit.Test;

import java.util.Date;

public class AcceptanceTest {
    @Test
    public void simpleAccounting() {
        AccountSystem book = new AccountSystem("randomName");
        final String anAccountName = "anAccountName";
        final String anotherAccountName = "anotherAccountName";
        Account anAccount = book.generateAccount(anAccountName, Account.AccountType.SHORT_TERM_ASSET);
        Account anotherAccount = book.generateAccount(anotherAccountName, Account.AccountType.SHORT_TERM_ASSET);
        Date today = new Date();
        Amount randomAmount = new Amount(100, Amount.Currency.EUR);
        ExecutedTransaction aTransaction = book.book("aTransaction", today, randomAmount, anAccount, anotherAccount);
        book.remove(aTransaction);
    }
}
