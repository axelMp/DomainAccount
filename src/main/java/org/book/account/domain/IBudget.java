package org.book.account.domain;

import java.util.Date;
import java.util.List;

public interface IBudget {
    List<IPlannedTransaction> getPlannedTransactions();

    /**
     * Forecasts closure of account based on currently known and planned transactions
     *
     * @param account    account to forecast
     * @param forecastOn forecast date
     * @return forecast amount for given account
     */
    Amount forecast(IAccount account, Date forecastOn);

    IPlannedTransaction plan(String narration, Period period, Amount amount, IAccount debitor, IAccount creditor, ExecutionOfPlannedTransaction executionOfPlannedTransaction);
}
