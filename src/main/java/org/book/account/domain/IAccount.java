package org.book.account.domain;

import java.util.Date;
import java.util.List;

public interface IAccount {
    AccountType getAccountType();

    Amount closure(Date date);

    // closure based only on transactions with other account
    Amount closure(Date date, IAccount relativeTo);

    List<ITransaction> getTransactions();

    String getName();

    List<IPlannedTransaction> getPlannedTransactions();
}
