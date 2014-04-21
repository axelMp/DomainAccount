package org.book.account.core;

import org.book.account.domain.Amount;
import org.book.account.domain.ExecutionOfPlannedTransaction;
import org.junit.Assert;
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

        sut.plan("randomString", randomDate, dayBeforeRandomDate, randomAmount, randomAccount, anotherRandomAccount, ExecutionOfPlannedTransaction.SINGLE);
    }

    @Test
    public void borderCase_noAssociatedTransactions_forecastsEmptyAmount() {
        Ledger ledger = new Ledger("randomName");
        Account anAccount = Utilities.generateRandomAccount(ledger);
        Date dateOfClosure = new Date();

        Assert.assertEquals(anAccount.forecast(dateOfClosure), Amount.noAmount());
    }

    @Test
    public void noAssociatedPlannedTransactions_forecastsIdenticalToClosure() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        ledger.book("aNarration", forecastOn, randomAmount, creditor, debitor);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void ignoresPlannedLinearlyProgressingTransactionWithEndDateBeforeNow() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        Date beforeToday = Utilities.previousDay(Utilities.today());
        ledger.book("aNarration", forecastOn, Utilities.generateRandomAmountInEuro(), creditor, debitor);
        ledger.getBudget().plan("aNarration", Utilities.previousDay(beforeToday), beforeToday, Utilities.generateRandomAmountInEuro(), creditor, debitor, ExecutionOfPlannedTransaction.LINEARLY_PROGRESSING);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void ignoresPlannedLinearlyProgressingTransactionWithStartDateAfterForecastDate() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        Date afterForeCastDay = Utilities.nextDay(forecastOn);
        ledger.book("aNarration", forecastOn, Utilities.generateRandomAmountInEuro(), creditor, debitor);
        ledger.getBudget().plan("aNarration", afterForeCastDay, Utilities.nextDay(afterForeCastDay), Utilities.generateRandomAmountInEuro(), creditor, debitor, ExecutionOfPlannedTransaction.LINEARLY_PROGRESSING);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void singleTransaction_plannedBetweenTodayAndForecast_forecastIncludesCompleteAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        ledger.getBudget().plan("aNarration", Utilities.nextDay(Utilities.today()), Utilities.previousDay(forecastOn), randomAmount, creditor, debitor, ExecutionOfPlannedTransaction.SINGLE);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), randomAmount);
    }

    @Test
    public void linearlyProgressingTransaction_plannedBetweenTodayAndForecast_forecastIncludesCompleteAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        ledger.getBudget().plan("aNarration", Utilities.nextDay(Utilities.today()), Utilities.previousDay(forecastOn), randomAmount, creditor, debitor, ExecutionOfPlannedTransaction.LINEARLY_PROGRESSING);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), randomAmount);
    }

    @Test
    public void singleTransaction_planStartsBeforeTodayAndEndsBetweenTodayAndForecast_forecastIncludesCompleteAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        ledger.getBudget().plan("aNarration", Utilities.previousDay(Utilities.today()), Utilities.previousDay(forecastOn), randomAmount, creditor, debitor, ExecutionOfPlannedTransaction.SINGLE);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), randomAmount);
    }

    @Test
    public void linearlyProgressingTransaction_planStartsBeforeTodayAndEndsBetweenTodayAndForecast_forecastIncludesPartialAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(10, Utilities.today());
        Date planStartsOn = Utilities.moveDay(-20, forecastOn);
        Date planEndsOn = forecastOn;

        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        Amount halfOfRandomAmount = new Amount(randomAmount.getCents() / 2, randomAmount.getCurrency());
        ledger.getBudget().plan("aNarration", planStartsOn, planEndsOn, randomAmount, creditor, debitor, ExecutionOfPlannedTransaction.LINEARLY_PROGRESSING);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), halfOfRandomAmount);
    }

    @Test
    public void singleTransaction_transactionWithSameNarrationHasBeenBookedInPlannedTime_forecastIgnoresPlannedTransaction() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        Amount anotherRandomAmount = Utilities.generateRandomAmountInEuro();
        String randomNarration = "aNarration";
        ledger.getBudget().plan(randomNarration, Utilities.previousDay(Utilities.today()), Utilities.previousDay(forecastOn), randomAmount, creditor, debitor, ExecutionOfPlannedTransaction.SINGLE);
        ledger.book(randomNarration, Utilities.today(), anotherRandomAmount, creditor, debitor);
        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void linearlyProgressingTransaction_transactionWithSameNarrationHasBeenBookedInPlannedTime_forecastIgnoresBooking() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(10, Utilities.today());
        Date planStartsOn = Utilities.moveDay(-20, forecastOn);
        Date planEndsOn = forecastOn;
        Amount plannedAmount = Utilities.generateRandomAmountInEuro();
        Amount bookedAmount = Utilities.generateRandomAmountInEuro();
        Amount halfPlannedAmount = new Amount(plannedAmount.getCents() / 2, plannedAmount.getCurrency());
        String randomNarration = "aNarration";
        ledger.getBudget().plan(randomNarration, planStartsOn, planEndsOn, plannedAmount, creditor, debitor, ExecutionOfPlannedTransaction.LINEARLY_PROGRESSING);
        ledger.book(randomNarration, Utilities.moveDay(1, planStartsOn), bookedAmount, creditor, debitor);
        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), Amount.add(halfPlannedAmount, bookedAmount));
    }

    @Test
    public void singleTransaction_planStartsBetweenTodayAndForecastAndEndsAfterForecast_forecastIncludesCompleteAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        ledger.getBudget().plan("aNarration", Utilities.nextDay(Utilities.today()), Utilities.nextDay(forecastOn), randomAmount, creditor, debitor, ExecutionOfPlannedTransaction.SINGLE);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), randomAmount);
    }

    @Test
    public void linearlyProgressingTransaction_planStartsBetweenTodayAndForecastAndEndsAfterForecast_forecastIncludesPartialAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(10, Utilities.today());
        Date planStartsOn = Utilities.moveDay(-5, forecastOn);
        Date planEndsOn = Utilities.moveDay(10, planStartsOn);
        Amount randomAmount = new Amount(2000, Amount.Currency.EUR);
        Amount halfRandomAmount = new Amount(randomAmount.getCents() / 2, randomAmount.getCurrency());

        ledger.getBudget().plan("aNarration", planStartsOn, planEndsOn, randomAmount, creditor, debitor, ExecutionOfPlannedTransaction.LINEARLY_PROGRESSING);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), halfRandomAmount);
    }

    @Test
    public void ignoresPlannedSingleTransactionWithEndDateBeforeNow() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        Date beforeToday = Utilities.previousDay(Utilities.today());
        ledger.book("aNarration", forecastOn, Utilities.generateRandomAmountInEuro(), creditor, debitor);
        ledger.getBudget().plan("aNarration", Utilities.previousDay(beforeToday), beforeToday, Utilities.generateRandomAmountInEuro(), creditor, debitor, ExecutionOfPlannedTransaction.SINGLE);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void ignoresPlannedSingleTransactionWithStartDateAfterForecastDate() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        Date afterForeCastDay = Utilities.nextDay(forecastOn);
        ledger.book("aNarration", forecastOn, Utilities.generateRandomAmountInEuro(), creditor, debitor);
        ledger.getBudget().plan("aNarration", afterForeCastDay, Utilities.nextDay(afterForeCastDay), Utilities.generateRandomAmountInEuro(), creditor, debitor, ExecutionOfPlannedTransaction.SINGLE);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), debitor.closure(forecastOn));
    }
}
