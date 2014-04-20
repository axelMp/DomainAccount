package org.book.account.core;


import org.book.account.domain.AccountType;
import org.book.account.domain.Amount;
import org.book.account.domain.ITransaction;
import org.junit.Test;

import java.util.Date;

public class AcceptanceTest {
    @Test
    public void simpleAccounting() {
        Ledger book = new Ledger("randomName");
        final String anAccountName = "anAccountName";
        final String anotherAccountName = "anotherAccountName";
        Account anAccount = book.createAccount(anAccountName, AccountType.ASSET);
        Account anotherAccount = book.createAccount(anotherAccountName, AccountType.ASSET);
        Date today = new Date();
        Amount randomAmount = new Amount(100, Amount.Currency.EUR);
        ITransaction aTransaction = book.book("aTransaction", today, randomAmount, anAccount, anotherAccount);
        book.remove(aTransaction);
    }
}
