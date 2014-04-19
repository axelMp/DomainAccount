package org.book.account.domain;


import java.util.Date;

public class ForecastService {
    public Amount forecastClosure(Ledger aLedger, Account anAccount, Budget plan, Date when) {
        return anAccount.closure(when);
    }
}
