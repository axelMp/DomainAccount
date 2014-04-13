package org.book.account.domain;


public interface ITransaction {
    String getNarration();

    Account getDebitor();

    Account getCreditor();
}
