package org.book.account.core;

import org.book.account.domain.Amount;
import org.book.account.domain.ExecutionOfPlannedTransaction;
import org.book.account.domain.Period;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class BudgetTest {

    @Test
    public void borderCase_noAssociatedTransactions_forecastsEmptyAmount() {
        Ledger ledger = new Ledger("randomName");
        Account anAccount = Utilities.generateRandomAccount(ledger);
        Date dateOfClosure = new Date();

        Assert.assertEquals(ledger.getBudget().forecast(anAccount, dateOfClosure), Amount.noAmount());
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
        Period period = new Period(Utilities.previousDay(beforeToday), beforeToday);
        ledger.getBudget().plan("aNarration", period, Utilities.generateRandomAmountInEuro(), creditor, debitor, ExecutionOfPlannedTransaction.LINEARLY_PROGRESSING);

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
        Period period = new Period(afterForeCastDay, Utilities.nextDay(afterForeCastDay));
        ledger.getBudget().plan("aNarration", period, Utilities.generateRandomAmountInEuro(), creditor, debitor, ExecutionOfPlannedTransaction.LINEARLY_PROGRESSING);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void singleTransaction_plannedBetweenTodayAndForecast_forecastIncludesCompleteAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        Period period = new Period(Utilities.nextDay(Utilities.today()), Utilities.previousDay(forecastOn));
        ledger.getBudget().plan("aNarration", period, randomAmount, creditor, debitor, ExecutionOfPlannedTransaction.SINGLE);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), randomAmount);
    }

    @Test
    public void linearlyProgressingTransaction_plannedBetweenTodayAndForecast_forecastIncludesCompleteAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        Period period = new Period(Utilities.nextDay(Utilities.today()), Utilities.previousDay(forecastOn));
        ledger.getBudget().plan("aNarration", period, randomAmount, creditor, debitor, ExecutionOfPlannedTransaction.LINEARLY_PROGRESSING);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), randomAmount);
    }

    @Test
    public void singleTransaction_planStartsBeforeTodayAndEndsBetweenTodayAndForecast_forecastIncludesCompleteAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        Period period = new Period(Utilities.previousDay(Utilities.today()), Utilities.previousDay(forecastOn));
        ledger.getBudget().plan("aNarration", period, randomAmount, creditor, debitor, ExecutionOfPlannedTransaction.SINGLE);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), randomAmount);
    }

    @Test
    public void linearlyProgressingTransaction_planStartsBeforeTodayAndEndsBetweenTodayAndForecast_forecastIncludesPartialAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(10, Utilities.today());
        Date planStartsOn = Utilities.moveDay(-20, forecastOn);

        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        Amount halfOfRandomAmount = new Amount(randomAmount.getCents() / 2, randomAmount.getCurrency());
        Period period = new Period(planStartsOn, forecastOn);
        ledger.getBudget().plan("aNarration", period, randomAmount, creditor, debitor, ExecutionOfPlannedTransaction.LINEARLY_PROGRESSING);

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
        Period period = new Period(Utilities.previousDay(Utilities.today()), Utilities.previousDay(forecastOn));
        ledger.getBudget().plan(randomNarration, period, randomAmount, creditor, debitor, ExecutionOfPlannedTransaction.SINGLE);
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
        Amount plannedAmount = Utilities.generateRandomAmountInEuro();
        Amount bookedAmount = Utilities.generateRandomAmountInEuro();
        Amount halfPlannedAmount = new Amount(plannedAmount.getCents() / 2, plannedAmount.getCurrency());
        String randomNarration = "aNarration";
        Period period = new Period(planStartsOn, forecastOn);
        ledger.getBudget().plan(randomNarration, period, plannedAmount, creditor, debitor, ExecutionOfPlannedTransaction.LINEARLY_PROGRESSING);
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
        Period period = new Period(Utilities.nextDay(Utilities.today()), Utilities.nextDay(forecastOn));
        ledger.getBudget().plan("aNarration", period, randomAmount, creditor, debitor, ExecutionOfPlannedTransaction.SINGLE);

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
        Period period = new Period(planStartsOn, planEndsOn);

        ledger.getBudget().plan("aNarration", period, randomAmount, creditor, debitor, ExecutionOfPlannedTransaction.LINEARLY_PROGRESSING);

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
        Period period = new Period(Utilities.previousDay(beforeToday), beforeToday);
        ledger.getBudget().plan("aNarration", period, Utilities.generateRandomAmountInEuro(), creditor, debitor, ExecutionOfPlannedTransaction.SINGLE);

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
        Period period = new Period(afterForeCastDay, Utilities.nextDay(afterForeCastDay));
        ledger.getBudget().plan("aNarration", period, Utilities.generateRandomAmountInEuro(), creditor, debitor, ExecutionOfPlannedTransaction.SINGLE);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), debitor.closure(forecastOn));
    }
}
