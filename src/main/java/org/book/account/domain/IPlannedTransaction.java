package org.book.account.domain;

import java.util.Date;

public interface IPlannedTransaction {
    String getNarration();

    IAccount getDebitor();

    IAccount getCreditor();

    Date getStartsOn();

    Date getEndsOn();

    ExecutionOfPlannedTransaction getExecutionOfPlannedTransaction();

    Amount forecast(Date date);

    boolean isApplicableForPeriod(Date from, Date until);
}
