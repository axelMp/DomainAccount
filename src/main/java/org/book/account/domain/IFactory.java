package org.book.account.domain;

/*
    Factory for aggregates as defined in Domain-Driven design
 */
public interface IFactory {
    ILedger createLedger(String name);
}
