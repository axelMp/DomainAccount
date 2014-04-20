package org.book.account.domain;

import java.util.Date;

public interface IAccount {
    AccountType getAccountType();

    Amount closure(Date date);
}
