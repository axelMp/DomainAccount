package org.book.account.domain;


import org.junit.Test;

import java.util.Date;

public class AcceptanceTest {
    @Test
    public void simpleAccounting() {
        Ledger book = new Ledger("randomName");
        final String anAccountName = "anAccountName";
        final String anotherAccountName = "anotherAccountName";
        Account anAccount = book.createAccount(anAccountName, Account.AccountType.ASSET);
        Account anotherAccount = book.createAccount(anotherAccountName, Account.AccountType.ASSET);
        Date today = new Date();
        Amount randomAmount = new Amount(100, Amount.Currency.EUR);
        Transaction aTransaction = book.book("aTransaction", today, randomAmount, anAccount, anotherAccount);
        book.remove(aTransaction);
    }
}
