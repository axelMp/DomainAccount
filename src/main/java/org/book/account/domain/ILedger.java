package org.book.account.domain;

import java.util.Date;
import java.util.List;

public interface ILedger {
    ITransaction book(String narration, Date occurredOn, Amount amount, IAccount debitor, IAccount creditor);

    IAccount createAccount(String name, AccountType accountType);

    List<IAccount> getAccounts();

    IBudget getBudget();

    List<ITransaction> getTransactions();
}
