package org.book.account.domain;


import java.util.Date;

public interface ITransaction {
    String getNarration();

    IAccount getDebitor();

    IAccount getCreditor();

    Date getOccurredOn();

    Amount getAmount();
}
