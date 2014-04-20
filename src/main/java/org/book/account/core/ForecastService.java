package org.book.account.core;

import org.book.account.domain.*;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;

class ForecastService {
    Amount forecastClosure(IAccount anAccount, Date forecastOn) {
        List<IPlannedTransaction> plannedTransactions = anAccount.getPlannedTransactions();
        List<ITransaction> transactions = anAccount.getTransactions();
        ListIterator<IPlannedTransaction> iterator1 = plannedTransactions.listIterator();
        Date today = new Date();
        while (iterator1.hasNext()) {
            IPlannedTransaction plannedTransaction1 = iterator1.next();
            if (!plannedTransaction1.isApplicableForPeriod(today, forecastOn)
                    || plannedTransaction1.matchesAnyPerformedTransaction(transactions)) {
                iterator1.remove();
            }
        }

        Amount currentClosure = anAccount.closure(today);
        Amount expectedClosure = sumPlannedTransactions(plannedTransactions, anAccount, forecastOn);
        return Amount.add(currentClosure, expectedClosure);
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
