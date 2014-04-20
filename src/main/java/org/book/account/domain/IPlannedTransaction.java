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

    boolean matchesTransaction(List<ITransaction> transactions);
}
