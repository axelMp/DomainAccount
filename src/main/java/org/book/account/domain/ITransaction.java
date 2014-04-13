package org.book.account.domain;


public interface ITransaction {
    String getDescription();

    Account getFrom();

    Account getTo();
}
