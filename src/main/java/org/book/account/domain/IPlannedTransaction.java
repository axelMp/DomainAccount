package org.book.account.domain;

import java.util.List;

public interface IPlannedTransaction {
    String getNarration();

    IAccount getDebitor();

    IAccount getCreditor();

    Schedule getSchedule();

    boolean matchesAnyPerformedTransaction(List<ITransaction> transactions);

    Amount forecast(Period aPeriod);
}
