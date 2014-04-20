package org.book.account.core;


public interface ITransaction {
    String getNarration();

    Account getDebitor();

    Account getCreditor();
}
