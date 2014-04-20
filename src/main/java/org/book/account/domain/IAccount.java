package org.book.account.domain;

import java.util.Date;
import java.util.List;

public interface IAccount {
    AccountType getAccountType();

    Amount closure(Date date);

    List<ITransaction> getTransactions();

    String getName();

    /**
     * Forecasts closure of account based on currently known and planned transactions
     *
     * @param forecastOn forecast date
     * @return forecast amount for given account
     */
    Amount forecast(Date forecastOn);

    List<IPlannedTransaction> getPlannedTransactions();
}
