package org.book.account.domain;

import java.util.List;

public interface IBudget {
    List<IPlannedTransaction> getPlannedTransactions();

    // TODO refactor to IAccount
    List<IPlannedTransaction> getPlannedTransactions(IAccount account);
}
