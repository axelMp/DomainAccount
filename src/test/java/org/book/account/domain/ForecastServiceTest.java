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
        Account anAccount = Utilities.generateRandomAccount(ledger);
        Account anotherAccount = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        ForecastService sut = new ForecastService();
        Budget plan = new Budget(ledger);
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        ledger.book("aNarration", forecastOn, randomAmount, anAccount, anotherAccount);

        Assert.assertEquals(sut.forecastClosure(ledger, anAccount, plan, forecastOn), anAccount.closure(forecastOn));
    }

    @Test
    public void ignoresPlannedContinuousTransactionWithEndDateBeforeNow() {
        Ledger ledger = new Ledger("randomName");
        Account anAccount = Utilities.generateRandomAccount(ledger);
        Account anotherAccount = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Date beforeToday = Utilities.previousDay(Utilities.today());
        ledger.book("aNarration", forecastOn, Utilities.generateRandomAmountInEuro(), anAccount, anotherAccount);
        budget.plan("aNarration", Utilities.previousDay(beforeToday), beforeToday, Utilities.generateRandomAmountInEuro(), anAccount, anotherAccount, true);

        Assert.assertEquals(sut.forecastClosure(ledger, anAccount, budget, forecastOn), anAccount.closure(forecastOn));
    }

    @Test
    public void ignoresPlannedContinuousTransactionWithStartDateAfterForecastDate() {
        Ledger ledger = new Ledger("randomName");
        Account anAccount = Utilities.generateRandomAccount(ledger);
        Account anotherAccount = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Date afterForeCastDay = Utilities.nextDay(forecastOn);
        ledger.book("aNarration", forecastOn, Utilities.generateRandomAmountInEuro(), anAccount, anotherAccount);
        budget.plan("aNarration", afterForeCastDay, Utilities.nextDay(afterForeCastDay), Utilities.generateRandomAmountInEuro(), anAccount, anotherAccount, true);

        Assert.assertEquals(sut.forecastClosure(ledger, anAccount, budget, forecastOn), anAccount.closure(forecastOn));
    }

    @Test
    public void ignoresPlannedNonContinuousTransactionWithEndDateBeforeNow() {
        Ledger ledger = new Ledger("randomName");
        Account anAccount = Utilities.generateRandomAccount(ledger);
        Account anotherAccount = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Date beforeToday = Utilities.previousDay(Utilities.today());
        ledger.book("aNarration", forecastOn, Utilities.generateRandomAmountInEuro(), anAccount, anotherAccount);
        budget.plan("aNarration", Utilities.previousDay(beforeToday), beforeToday, Utilities.generateRandomAmountInEuro(), anAccount, anotherAccount, false);

        Assert.assertEquals(sut.forecastClosure(ledger, anAccount, budget, forecastOn), anAccount.closure(forecastOn));
    }

    @Test
    public void ignoresPlannedNonContinuousTransactionWithStartDateAfterForecastDate() {
        Ledger ledger = new Ledger("randomName");
        Account anAccount = Utilities.generateRandomAccount(ledger);
        Account anotherAccount = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Date afterForeCastDay = Utilities.nextDay(forecastOn);
        ledger.book("aNarration", forecastOn, Utilities.generateRandomAmountInEuro(), anAccount, anotherAccount);
        budget.plan("aNarration", afterForeCastDay, Utilities.nextDay(afterForeCastDay), Utilities.generateRandomAmountInEuro(), anAccount, anotherAccount, false);

        Assert.assertEquals(sut.forecastClosure(ledger, anAccount, budget, forecastOn), anAccount.closure(forecastOn));
    }
}
