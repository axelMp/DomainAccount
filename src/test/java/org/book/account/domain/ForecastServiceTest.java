package org.book.account.domain;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class ForecastServiceTest {
    @Test
    public void borderCase_noAssociatedTransactions_forecastsEmptyAmount() {
        Ledger ledger = new Ledger("randomName");
        Account anAccount = Utilities.generateRandomAccount(ledger);
        Date dateOfClosure = new Date();
        ForecastService sut = new ForecastService();
        Budget plan = new Budget(ledger);

        Assert.assertEquals(sut.forecastClosure(ledger, anAccount, plan, dateOfClosure), Amount.noAmount());
    }

    @Test
    public void noAssociatedPlannedTransactions_forecastsIdenticalToClosure() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        ForecastService sut = new ForecastService();
        Budget plan = new Budget(ledger);
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        ledger.book("aNarration", forecastOn, randomAmount, creditor, debitor);

        Assert.assertEquals(sut.forecastClosure(ledger, debitor, plan, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void ignoresPlannedContinuousTransactionWithEndDateBeforeNow() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Date beforeToday = Utilities.previousDay(Utilities.today());
        ledger.book("aNarration", forecastOn, Utilities.generateRandomAmountInEuro(), creditor, debitor);
        budget.plan("aNarration", Utilities.previousDay(beforeToday), beforeToday, Utilities.generateRandomAmountInEuro(), creditor, debitor, true);

        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void ignoresPlannedContinuousTransactionWithStartDateAfterForecastDate() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Date afterForeCastDay = Utilities.nextDay(forecastOn);
        ledger.book("aNarration", forecastOn, Utilities.generateRandomAmountInEuro(), creditor, debitor);
        budget.plan("aNarration", afterForeCastDay, Utilities.nextDay(afterForeCastDay), Utilities.generateRandomAmountInEuro(), creditor, debitor, true);

        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void nonContinuousTransaction_plannedBetweenTodayAndForecast_forecastIncludesCompleteAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        budget.plan("aNarration", Utilities.nextDay(Utilities.today()), Utilities.previousDay(forecastOn), randomAmount, creditor, debitor, false);

        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), randomAmount);
    }

    @Test
    public void nonContinuousTransaction_planStartsBeforeTodayAndEndsBetweenTodayAndForecast_forecastIncludesCompleteAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        budget.plan("aNarration", Utilities.previousDay(Utilities.today()), Utilities.previousDay(forecastOn), randomAmount, creditor, debitor, false);

        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), randomAmount);
    }

    @Test
    public void nonContinuousTransaction_transactionWithSameNarrationHasBeenBookedInPlannedTime_forecastIgnoresPlannedTransaction() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        Amount anotherRandomAmount = Utilities.generateRandomAmountInEuro();
        String randomNarration = "aNarration";
        budget.plan(randomNarration, Utilities.previousDay(Utilities.today()), Utilities.previousDay(forecastOn), randomAmount, creditor, debitor, false);
        ledger.book(randomNarration, Utilities.today(), anotherRandomAmount, creditor, debitor);
        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void nonContinuousTransaction_planStartsBetweenTodayAndForecastAndEndsAfterForecast_forecastIncludesCompleteAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        budget.plan("aNarration", Utilities.nextDay(Utilities.today()), Utilities.nextDay(forecastOn), randomAmount, creditor, debitor, false);

        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), randomAmount);
    }

    @Test
    public void ignoresPlannedNonContinuousTransactionWithEndDateBeforeNow() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Date beforeToday = Utilities.previousDay(Utilities.today());
        ledger.book("aNarration", forecastOn, Utilities.generateRandomAmountInEuro(), creditor, debitor);
        budget.plan("aNarration", Utilities.previousDay(beforeToday), beforeToday, Utilities.generateRandomAmountInEuro(), creditor, debitor, false);

        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void ignoresPlannedNonContinuousTransactionWithStartDateAfterForecastDate() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Date afterForeCastDay = Utilities.nextDay(forecastOn);
        ledger.book("aNarration", forecastOn, Utilities.generateRandomAmountInEuro(), creditor, debitor);
        budget.plan("aNarration", afterForeCastDay, Utilities.nextDay(afterForeCastDay), Utilities.generateRandomAmountInEuro(), creditor, debitor, false);

        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), debitor.closure(forecastOn));
    }
}
