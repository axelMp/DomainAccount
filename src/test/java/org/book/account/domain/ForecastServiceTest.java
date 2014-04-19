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
    public void ignoresPlannedLinearlyProgressingTransactionWithEndDateBeforeNow() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Date beforeToday = Utilities.previousDay(Utilities.today());
        ledger.book("aNarration", forecastOn, Utilities.generateRandomAmountInEuro(), creditor, debitor);
        budget.planLinearlyProgressingTransaction("aNarration", Utilities.previousDay(beforeToday), beforeToday, Utilities.generateRandomAmountInEuro(), creditor, debitor);

        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void ignoresPlannedLinearlyProgressingTransactionWithStartDateAfterForecastDate() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Date afterForeCastDay = Utilities.nextDay(forecastOn);
        ledger.book("aNarration", forecastOn, Utilities.generateRandomAmountInEuro(), creditor, debitor);
        budget.planLinearlyProgressingTransaction("aNarration", afterForeCastDay, Utilities.nextDay(afterForeCastDay), Utilities.generateRandomAmountInEuro(), creditor, debitor);

        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void singleTransaction_plannedBetweenTodayAndForecast_forecastIncludesCompleteAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        budget.planSingleTransaction("aNarration", Utilities.nextDay(Utilities.today()), Utilities.previousDay(forecastOn), randomAmount, creditor, debitor);

        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), randomAmount);
    }

    @Test
    public void linearlyProgressingTransaction_plannedBetweenTodayAndForecast_forecastIncludesCompleteAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        budget.planLinearlyProgressingTransaction("aNarration", Utilities.nextDay(Utilities.today()), Utilities.previousDay(forecastOn), randomAmount, creditor, debitor);

        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), randomAmount);
    }

    @Test
    public void singleTransaction_planStartsBeforeTodayAndEndsBetweenTodayAndForecast_forecastIncludesCompleteAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        budget.planSingleTransaction("aNarration", Utilities.previousDay(Utilities.today()), Utilities.previousDay(forecastOn), randomAmount, creditor, debitor);

        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), randomAmount);
    }

    @Test
    public void linearlyProgressingTransaction_planStartsBeforeTodayAndEndsBetweenTodayAndForecast_forecastIncludesPartialAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(10, Utilities.today());
        Date planStartsOn = Utilities.moveDay(-20, forecastOn);
        Date planEndsOn = forecastOn;

        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        Amount halfOfRandomAmount = new Amount(randomAmount.getCents() / 2, randomAmount.getCurrency());
        budget.planLinearlyProgressingTransaction("aNarration", planStartsOn, planEndsOn, randomAmount, creditor, debitor);

        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), halfOfRandomAmount);
    }

    @Test
    public void singleTransaction_transactionWithSameNarrationHasBeenBookedInPlannedTime_forecastIgnoresPlannedTransaction() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        Amount anotherRandomAmount = Utilities.generateRandomAmountInEuro();
        String randomNarration = "aNarration";
        budget.planSingleTransaction(randomNarration, Utilities.previousDay(Utilities.today()), Utilities.previousDay(forecastOn), randomAmount, creditor, debitor);
        ledger.book(randomNarration, Utilities.today(), anotherRandomAmount, creditor, debitor);
        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void linearlyProgressingTransaction_transactionWithSameNarrationHasBeenBookedInPlannedTime_forecastIgnoresBooking() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(10, Utilities.today());
        Date planStartsOn = Utilities.moveDay(-20, forecastOn);
        Date planEndsOn = forecastOn;
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Amount plannedAmount = Utilities.generateRandomAmountInEuro();
        Amount bookedAmount = Utilities.generateRandomAmountInEuro();
        Amount halfPlannedAmount = new Amount(plannedAmount.getCents() / 2, plannedAmount.getCurrency());
        String randomNarration = "aNarration";
        budget.planLinearlyProgressingTransaction(randomNarration, planStartsOn, planEndsOn, plannedAmount, creditor, debitor);
        ledger.book(randomNarration, Utilities.moveDay(1, planStartsOn), bookedAmount, creditor, debitor);
        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), Amount.add(halfPlannedAmount, bookedAmount));
    }

    @Test
    public void singleTransaction_planStartsBetweenTodayAndForecastAndEndsAfterForecast_forecastIncludesCompleteAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Amount randomAmount = Utilities.generateRandomAmountInEuro();
        budget.planSingleTransaction("aNarration", Utilities.nextDay(Utilities.today()), Utilities.nextDay(forecastOn), randomAmount, creditor, debitor);

        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), randomAmount);
    }

    @Test
    public void linearlyProgressingTransaction_planStartsBetweenTodayAndForecastAndEndsAfterForecast_forecastIncludesPartialAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(10, Utilities.today());
        Date planStartsOn = Utilities.moveDay(-5, forecastOn);
        Date planEndsOn = Utilities.moveDay(10, planStartsOn);
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Amount randomAmount = new Amount(2000, Amount.Currency.EUR);
        Amount halfRandomAmount = new Amount(randomAmount.getCents() / 2, randomAmount.getCurrency());

        budget.planLinearlyProgressingTransaction("aNarration", planStartsOn, planEndsOn, randomAmount, creditor, debitor);

        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), halfRandomAmount);
    }

    @Test
    public void ignoresPlannedSingleTransactionWithEndDateBeforeNow() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Date beforeToday = Utilities.previousDay(Utilities.today());
        ledger.book("aNarration", forecastOn, Utilities.generateRandomAmountInEuro(), creditor, debitor);
        budget.planSingleTransaction("aNarration", Utilities.previousDay(beforeToday), beforeToday, Utilities.generateRandomAmountInEuro(), creditor, debitor);

        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void ignoresPlannedSingleTransactionWithStartDateAfterForecastDate() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = Utilities.generateRandomAccount(ledger);
        Account debitor = Utilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        ForecastService sut = new ForecastService();
        Budget budget = new Budget(ledger);
        Date afterForeCastDay = Utilities.nextDay(forecastOn);
        ledger.book("aNarration", forecastOn, Utilities.generateRandomAmountInEuro(), creditor, debitor);
        budget.planSingleTransaction("aNarration", afterForeCastDay, Utilities.nextDay(afterForeCastDay), Utilities.generateRandomAmountInEuro(), creditor, debitor);

        Assert.assertEquals(sut.forecastClosure(ledger, debitor, budget, forecastOn), debitor.closure(forecastOn));
    }
}
