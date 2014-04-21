package org.book.account.core;

import org.book.account.Utilities;
import org.book.account.domain.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class BudgetTest {

    @Test
    public void borderCase_noAssociatedTransactions_forecastsEmptyAmount() {
        Ledger ledger = new Ledger("randomName");
        Account anAccount = CoreUtilities.generateRandomAccount(ledger);
        Date dateOfClosure = new Date();

        Assert.assertEquals(ledger.getBudget().forecast(anAccount, dateOfClosure), Amount.noAmount());
    }

    @Test
    public void noAssociatedPlannedTransactions_forecastsIdenticalToClosure() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = CoreUtilities.generateRandomAccount(ledger);
        Account debitor = CoreUtilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        Amount randomAmount = DomainUtilities.generateRandomAmountInEuro();
        ledger.book("aNarration", forecastOn, randomAmount, creditor, debitor);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void ignoresPlannedLinearlyProgressingTransactionWithEndDateBeforeNow() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = CoreUtilities.generateRandomAccount(ledger);
        Account debitor = CoreUtilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        Date beforeToday = Utilities.previousDay(Utilities.today());
        ledger.book("aNarration", forecastOn, DomainUtilities.generateRandomAmountInEuro(), creditor, debitor);
        Schedule schedule = new Schedule(new Period(Utilities.previousDay(beforeToday), beforeToday), ExecutionPolicy.LINEARLY_PROGRESSING);
        ledger.getBudget().plan("aNarration", DomainUtilities.generateRandomAmountInEuro(), creditor, debitor, schedule);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void ignoresPlannedLinearlyProgressingTransactionWithStartDateAfterForecastDate() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = CoreUtilities.generateRandomAccount(ledger);
        Account debitor = CoreUtilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        Date afterForeCastDay = Utilities.nextDay(forecastOn);
        ledger.book("aNarration", forecastOn, DomainUtilities.generateRandomAmountInEuro(), creditor, debitor);
        Schedule schedule = new Schedule(new Period(afterForeCastDay, Utilities.nextDay(afterForeCastDay)), ExecutionPolicy.LINEARLY_PROGRESSING);
        ledger.getBudget().plan("aNarration", DomainUtilities.generateRandomAmountInEuro(), creditor, debitor, schedule);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void singleTransaction_plannedBetweenTodayAndForecast_forecastIncludesCompleteAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = CoreUtilities.generateRandomAccount(ledger);
        Account debitor = CoreUtilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        Amount randomAmount = DomainUtilities.generateRandomAmountInEuro();
        Schedule schedule = new Schedule(new Period(Utilities.nextDay(Utilities.today()), Utilities.previousDay(forecastOn)), ExecutionPolicy.SINGLE);
        ledger.getBudget().plan("aNarration", randomAmount, creditor, debitor, schedule);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), randomAmount);
    }

    @Test
    public void linearlyProgressingTransaction_plannedBetweenTodayAndForecast_forecastIncludesCompleteAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = CoreUtilities.generateRandomAccount(ledger);
        Account debitor = CoreUtilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        Amount randomAmount = DomainUtilities.generateRandomAmountInEuro();
        Schedule schedule = new Schedule(new Period(Utilities.nextDay(Utilities.today()), Utilities.previousDay(forecastOn)), ExecutionPolicy.LINEARLY_PROGRESSING);
        ledger.getBudget().plan("aNarration", randomAmount, creditor, debitor, schedule);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), randomAmount);
    }

    @Test
    public void singleTransaction_planStartsBeforeTodayAndEndsBetweenTodayAndForecast_forecastIncludesCompleteAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = CoreUtilities.generateRandomAccount(ledger);
        Account debitor = CoreUtilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        Amount randomAmount = DomainUtilities.generateRandomAmountInEuro();
        Schedule schedule = new Schedule(new Period(Utilities.previousDay(Utilities.today()), Utilities.previousDay(forecastOn)), ExecutionPolicy.SINGLE);
        ledger.getBudget().plan("aNarration", randomAmount, creditor, debitor, schedule);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), randomAmount);
    }

    @Test
    public void linearlyProgressingTransaction_planStartsBeforeTodayAndEndsBetweenTodayAndForecast_forecastIncludesPartialAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = CoreUtilities.generateRandomAccount(ledger);
        Account debitor = CoreUtilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(10, Utilities.today());
        Date planStartsOn = Utilities.moveDay(-20, forecastOn);

        Amount randomAmount = DomainUtilities.generateRandomAmountInEuro();
        Amount halfOfRandomAmount = new Amount(randomAmount.getCents() / 2, randomAmount.getCurrency());
        Schedule schedule = new Schedule(new Period(planStartsOn, forecastOn), ExecutionPolicy.LINEARLY_PROGRESSING);
        ledger.getBudget().plan("aNarration", randomAmount, creditor, debitor, schedule);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), halfOfRandomAmount);
    }

    @Test
    public void singleTransaction_transactionWithSameNarrationHasBeenBookedInPlannedTime_forecastIgnoresPlannedTransaction() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = CoreUtilities.generateRandomAccount(ledger);
        Account debitor = CoreUtilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        Amount randomAmount = DomainUtilities.generateRandomAmountInEuro();
        Amount anotherRandomAmount = DomainUtilities.generateRandomAmountInEuro();
        String randomNarration = "aNarration";
        Schedule schedule = new Schedule(new Period(Utilities.previousDay(Utilities.today()), Utilities.previousDay(forecastOn)), ExecutionPolicy.SINGLE);
        ledger.getBudget().plan(randomNarration, randomAmount, creditor, debitor, schedule);
        ledger.book(randomNarration, Utilities.today(), anotherRandomAmount, creditor, debitor);
        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void linearlyProgressingTransaction_transactionWithSameNarrationHasBeenBookedInPlannedTime_forecastIgnoresBooking() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = CoreUtilities.generateRandomAccount(ledger);
        Account debitor = CoreUtilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(10, Utilities.today());
        Date planStartsOn = Utilities.moveDay(-20, forecastOn);
        Amount plannedAmount = DomainUtilities.generateRandomAmountInEuro();
        Amount bookedAmount = DomainUtilities.generateRandomAmountInEuro();
        Amount halfPlannedAmount = new Amount(plannedAmount.getCents() / 2, plannedAmount.getCurrency());
        String randomNarration = "aNarration";
        Schedule schedule = new Schedule(new Period(planStartsOn, forecastOn), ExecutionPolicy.LINEARLY_PROGRESSING);
        ledger.getBudget().plan(randomNarration, plannedAmount, creditor, debitor, schedule);
        ledger.book(randomNarration, Utilities.moveDay(1, planStartsOn), bookedAmount, creditor, debitor);
        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), Amount.add(halfPlannedAmount, bookedAmount));
    }

    @Test
    public void singleTransaction_planStartsBetweenTodayAndForecastAndEndsAfterForecast_forecastIncludesCompleteAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = CoreUtilities.generateRandomAccount(ledger);
        Account debitor = CoreUtilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(20, Utilities.today());
        Amount randomAmount = DomainUtilities.generateRandomAmountInEuro();
        Schedule schedule = new Schedule(new Period(Utilities.nextDay(Utilities.today()), Utilities.nextDay(forecastOn)), ExecutionPolicy.SINGLE);
        ledger.getBudget().plan("aNarration", randomAmount, creditor, debitor, schedule);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), randomAmount);
    }

    @Test
    public void linearlyProgressingTransaction_planStartsBetweenTodayAndForecastAndEndsAfterForecast_forecastIncludesPartialAmount() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = CoreUtilities.generateRandomAccount(ledger);
        Account debitor = CoreUtilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.moveDay(10, Utilities.today());
        Date planStartsOn = Utilities.moveDay(-5, forecastOn);
        Date planEndsOn = Utilities.moveDay(10, planStartsOn);
        Amount randomAmount = new Amount(2000, Amount.Currency.EUR);
        Amount halfRandomAmount = new Amount(randomAmount.getCents() / 2, randomAmount.getCurrency());
        Schedule schedule = new Schedule(new Period(planStartsOn, planEndsOn), ExecutionPolicy.LINEARLY_PROGRESSING);

        ledger.getBudget().plan("aNarration", randomAmount, creditor, debitor, schedule);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), halfRandomAmount);
    }

    @Test
    public void ignoresPlannedSingleTransactionWithEndDateBeforeNow() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = CoreUtilities.generateRandomAccount(ledger);
        Account debitor = CoreUtilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        Date beforeToday = Utilities.previousDay(Utilities.today());
        ledger.book("aNarration", forecastOn, DomainUtilities.generateRandomAmountInEuro(), creditor, debitor);
        Schedule schedule = new Schedule(new Period(Utilities.previousDay(beforeToday), beforeToday), ExecutionPolicy.SINGLE);
        ledger.getBudget().plan("aNarration", DomainUtilities.generateRandomAmountInEuro(), creditor, debitor, schedule);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), debitor.closure(forecastOn));
    }

    @Test
    public void ignoresPlannedSingleTransactionWithStartDateAfterForecastDate() {
        Ledger ledger = new Ledger("randomName");
        Account creditor = CoreUtilities.generateRandomAccount(ledger);
        Account debitor = CoreUtilities.generateRandomAccount(ledger);
        Date forecastOn = Utilities.today();
        Date afterForeCastDay = Utilities.nextDay(forecastOn);
        ledger.book("aNarration", forecastOn, DomainUtilities.generateRandomAmountInEuro(), creditor, debitor);
        Schedule schedule = new Schedule(new Period(afterForeCastDay, Utilities.nextDay(afterForeCastDay)), ExecutionPolicy.SINGLE);
        ledger.getBudget().plan("aNarration", DomainUtilities.generateRandomAmountInEuro(), creditor, debitor, schedule);

        Assert.assertEquals(ledger.getBudget().forecast(debitor, forecastOn), debitor.closure(forecastOn));
    }
}
