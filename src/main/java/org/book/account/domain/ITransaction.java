package org.book.account.domain;


public interface ITransaction {
    String getNarration();

    IAccount getDebitor();

    IAccount getCreditor();
}
