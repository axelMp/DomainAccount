package org.book.account.domain;


import java.util.Date;
import java.util.List;
import java.util.ListIterator;

public class ForecastService {
    public Amount forecastClosure(Ledger aLedger, Account anAccount, Budget plan, Date forecastOn) {
        List<PlannedTransaction> plannedTransactions = plan.getPlannedTransactions(anAccount);
        removePlansOutsideNowAndForecast(plannedTransactions, forecastOn);
        removeNonContinuousPlansWithMatchingTransaction(plannedTransactions, aLedger.getTransactions(anAccount));
        Date today = new Date();
        Amount currentClosure = anAccount.closure(today);
        Amount expectedClosure = sumPlannedTransactions(plannedTransactions, anAccount, forecastOn);
        return Amount.add(currentClosure, expectedClosure);
    }

    private void removeNonContinuousPlansWithMatchingTransaction(List<PlannedTransaction> plannedTransactions, List<Transaction> transactions) {
        ListIterator<PlannedTransaction> iterator = plannedTransactions.listIterator();
        while (iterator.hasNext()) {
            PlannedTransaction plannedTransaction = iterator.next();
            if (plannedTransaction.isContinuous()) {
                continue;
            }
            boolean matchingTransactionFound = false;
            for (Transaction transaction : transactions) {
                boolean identicalNarration = transaction.getNarration().equals(plannedTransaction.getNarration());
                boolean tookPlaceAfterPlannedStartsOn = !transaction.getOccurredOn().before(plannedTransaction.getStartsOn());
                boolean tookPlaceBeforePlannedEndsOn = !transaction.getOccurredOn().after(plannedTransaction.getEndsOn());
                if (identicalNarration && tookPlaceAfterPlannedStartsOn && tookPlaceBeforePlannedEndsOn) {
                    matchingTransactionFound = true;
                }
            }

            if (matchingTransactionFound) {
                iterator.remove();
            }
        }
    }

    private void removePlansOutsideNowAndForecast(List<PlannedTransaction> plannedTransactions, Date forecastOn) {
        ListIterator<PlannedTransaction> iterator = plannedTransactions.listIterator();
        Date today = new Date();
        while (iterator.hasNext()) {
            PlannedTransaction plannedTransaction = iterator.next();
            boolean planAlreadyOverdue = today.after(plannedTransaction.getEndsOn());
            boolean planExpectedAfterForecast = plannedTransaction.getStartsOn().after(forecastOn);
            if (planAlreadyOverdue || planExpectedAfterForecast) {
                iterator.remove();
            }
        }
    }

    private Amount sumPlannedTransactions(List<PlannedTransaction> plan, Account anAccount, Date forecastOn) {
        Amount sum = Amount.noAmount();
        for (PlannedTransaction plannedTransaction : plan) {
            if (anAccount.equals(plannedTransaction.getCreditor())) {
                sum = Amount.add(sum, plannedTransaction.valueAt(forecastOn));
            } else {
                sum = Amount.subtract(sum, plannedTransaction.valueAt(forecastOn));
            }
        }
        return sum;
    }
}
