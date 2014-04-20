package org.book.account.core;

import org.junit.Test;

import java.util.Date;

public class BudgetTest {
    @Test(expected = IllegalArgumentException.class)
    public void transactionIsPlannedToEndBeforeItStarted_IllegalArgumentException() {
        Ledger ledger = new Ledger("randomName");
        Budget sut = new Budget(ledger);
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        Account randomAccount = Utilities.generateRandomAccount(ledger);
        Account anotherRandomAccount = Utilities.generateRandomAccount(ledger);

        Date randomDate = Utilities.today();
        Date dayBeforeRandomDate = Utilities.previousDay(randomDate);

        sut.planLinearlyProgressingTransaction("randomString", randomDate, dayBeforeRandomDate, randomAmount, randomAccount, anotherRandomAccount);
    }
}
