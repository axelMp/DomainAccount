package org.book.account.domain;


public interface Transaction {
    String getNarration();

    Account getDebitor();

    Account getCreditor();
}
