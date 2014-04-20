package org.book.account.domain;

import java.util.Date;
import java.util.List;

public interface IPlannedTransaction {
    String getNarration();

    IAccount getDebitor();

    IAccount getCreditor();

    Date getStartsOn();

    Date getEndsOn();

    ExecutionOfPlannedTransaction getExecutionOfPlannedTransaction();

    Amount forecast(Date date);

    boolean isApplicableForPeriod(Date from, Date until);

    boolean matchesAnyPerformedTransaction(List<ITransaction> transactions);

    Amount forecast(Date from, Date until);
}
