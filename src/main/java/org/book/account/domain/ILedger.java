package org.book.account.domain;

import java.util.Date;
import java.util.List;

public interface ILedger {
    ITransaction book(String narration, Date occurredOn, Amount amount, IAccount debitor, IAccount creditor);

    List<IAccount> getAccounts();

    List<ITransaction> getTransactions();
}