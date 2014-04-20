package org.book.account.core;

import org.book.account.domain.Amount;
import org.book.account.domain.IAccount;
import org.book.account.domain.IPlannedTransaction;
import org.book.account.domain.ITransaction;

import java.util.Date;
import java.util.List;

class ForecastService {
    Amount forecastClosure(IAccount anAccount, Date forecastOn) {
        List<IPlannedTransaction> plannedTransactions = anAccount.getPlannedTransactions();
        List<ITransaction> transactions = anAccount.getTransactions();
        Date today = new Date();
        Amount currentClosure = anAccount.closure(today);
        Amount expectedClosure = Amount.noAmount();

        for (IPlannedTransaction plannedTransaction : plannedTransactions) {
            if (!plannedTransaction.matchesAnyPerformedTransaction(transactions)) {
                Amount forecastOfPlannedTransaction = plannedTransaction.forecast(today, forecastOn);

                if (anAccount.equals(plannedTransaction.getCreditor())) {
                    expectedClosure = Amount.add(expectedClosure, forecastOfPlannedTransaction);
                } else {
                    expectedClosure = Amount.subtract(expectedClosure, forecastOfPlannedTransaction);
                }
            }
        }
        return Amount.add(currentClosure, expectedClosure);
    }
}
