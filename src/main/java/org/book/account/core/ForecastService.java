package org.book.account.core;

import org.book.account.domain.*;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;

class ForecastService {
    Amount forecastClosure(IAccount anAccount, Date forecastOn) {
        List<IPlannedTransaction> plannedTransactions = anAccount.getPlannedTransactions();
        removePlansOutsideNowAndForecast(plannedTransactions, forecastOn);
        removeSingledPlannedTransactionsWithMatchingTransaction(plannedTransactions, anAccount.getTransactions());
        Date today = new Date();
        Amount currentClosure = anAccount.closure(today);
        Amount expectedClosure = sumPlannedTransactions(plannedTransactions, anAccount, forecastOn);
        return Amount.add(currentClosure, expectedClosure);
    }

    private void removeSingledPlannedTransactionsWithMatchingTransaction(List<IPlannedTransaction> plannedTransactions, List<ITransaction> transactions) {
        ListIterator<IPlannedTransaction> iterator = plannedTransactions.listIterator();
        while (iterator.hasNext()) {
            IPlannedTransaction plannedTransaction = iterator.next();
            if (plannedTransaction.matchesAnyPerformedTransaction(transactions)) {
                iterator.remove();
            }
        }
    }

    private void removePlansOutsideNowAndForecast(List<IPlannedTransaction> plannedTransactions, Date forecastOn) {
        ListIterator<IPlannedTransaction> iterator = plannedTransactions.listIterator();
        Date today = new Date();
        while (iterator.hasNext()) {
            IPlannedTransaction plannedTransaction = iterator.next();
            if (!plannedTransaction.isApplicableForPeriod(today, forecastOn)) {
                iterator.remove();
            }
        }
    }

    private Amount sumPlannedTransactions(List<IPlannedTransaction> plan, IAccount anAccount, Date forecastOn) {
        Amount sum = Amount.noAmount();
        Date today = new Date();
        for (IPlannedTransaction plannedTransaction : plan) {
            Amount forecastOfPlannedTransaction = plannedTransaction.forecast(forecastOn);
            if (plannedTransaction.getExecutionOfPlannedTransaction().equals(ExecutionOfPlannedTransaction.LINEARLY_PROGRESSING)) {
                forecastOfPlannedTransaction = Amount.subtract(forecastOfPlannedTransaction, plannedTransaction.forecast(today));
            }
            if (anAccount.equals(plannedTransaction.getCreditor())) {
                sum = Amount.add(sum, forecastOfPlannedTransaction);
            } else {
                sum = Amount.subtract(sum, forecastOfPlannedTransaction);
            }
        }
        return sum;
    }
}
