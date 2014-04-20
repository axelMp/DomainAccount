package org.book.account.domain;

import java.util.Date;
import java.util.List;

public interface IBudget {
    List<IPlannedTransaction> getPlannedTransactions();

    IPlannedTransaction plan(String narration, Date startsOn, Date endsOn, Amount amount, IAccount debitor, IAccount creditor, ExecutionOfPlannedTransaction executionOfPlannedTransaction);
}
