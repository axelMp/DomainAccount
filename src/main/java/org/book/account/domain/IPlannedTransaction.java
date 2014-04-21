package org.book.account.domain;

import java.util.List;

public interface IPlannedTransaction {
    String getNarration();

    IAccount getDebitor();

    IAccount getCreditor();

    Period getPeriod();

    boolean matchesAnyPerformedTransaction(List<ITransaction> transactions);

    Amount forecast(Period aPeriod);
}
