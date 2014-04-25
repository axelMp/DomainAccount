package org.book.account.domain;

public interface IPlannedTransaction {
    String getNarration();

    IAccount getDebitor();

    IAccount getCreditor();

    Schedule getSchedule();

    Amount forecast(Period aPeriod);

    Long getId();
}
