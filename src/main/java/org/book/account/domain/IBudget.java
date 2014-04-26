package org.book.account.domain;

import java.util.Date;
import java.util.List;

public interface IBudget {
    List<IPlannedTransaction> getPlannedTransactions();

    /**
     * Forecasts closure of account based on currently known and planned transactions
     */
    Amount forecast(IAccount account, Date forecastOn, MatchingPolicy matchingPolicy);

    /**
     * Forecasts closure of account based on currently known and planned transactions involving the specified account
     */
    Amount forecast(IAccount account, IAccount relativeTo, Date forecastOn, MatchingPolicy matchingPolicy);


    IPlannedTransaction plan(String narration, Amount amount, IAccount debitor, IAccount creditor, Schedule schedule);
}
