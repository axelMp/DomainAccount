package org.book.account.core;

import org.book.account.domain.Amount;
import org.book.account.domain.IAccount;
import org.book.account.domain.IPlannedTransaction;
import org.book.account.domain.ITransaction;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;

class ForecastService {
    Amount forecastClosure(IAccount anAccount, Date forecastOn) {
        List<IPlannedTransaction> plannedTransactions = anAccount.getPlannedTransactions();
        List<ITransaction> transactions = anAccount.getTransactions();
        ListIterator<IPlannedTransaction> iterator = plannedTransactions.listIterator();
        Date today = new Date();
        while (iterator.hasNext()) {
            IPlannedTransaction plannedTransaction1 = iterator.next();
            if (!plannedTransaction1.isApplicableForPeriod(today, forecastOn)
                    || plannedTransaction1.matchesAnyPerformedTransaction(transactions)) {
                iterator.remove();
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
            Amount forecastOfPlannedTransaction = plannedTransaction.forecast(today, forecastOn);

            if (anAccount.equals(plannedTransaction.getCreditor())) {
                sum = Amount.add(sum, forecastOfPlannedTransaction);
            } else {
                sum = Amount.subtract(sum, forecastOfPlannedTransaction);
            }
        }
        return sum;
    }
}
