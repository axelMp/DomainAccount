package org.book.account.domain;

import java.util.Date;
import java.util.List;

public interface IAccount {
    AccountType getAccountType();

    Amount closure(Date date);

    List<ITransaction> getTransactions();

    String getName();


    List<IPlannedTransaction> getPlannedTransactions();
}
