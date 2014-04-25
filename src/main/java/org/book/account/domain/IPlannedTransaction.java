package org.book.account.domain;

public interface IPlannedTransaction {
    String getNarration();

    void setNarration(String narration);

    IAccount getDebitor();

    void setDebitor(IAccount debitor);

    IAccount getCreditor();

    void setCreditor(IAccount creditor);

    Schedule getSchedule();

    void setSchedule(Schedule schedule);

    Amount forecast(Period aPeriod);

    Long getId();

    void setAmount(Amount amount);
}
