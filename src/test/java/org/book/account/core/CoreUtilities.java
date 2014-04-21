package org.book.account.core;

import org.book.account.domain.AccountType;

import java.util.UUID;

public class CoreUtilities {

    static Account generateRandomAccount(Ledger aLedger) {
        final String anAccountName = UUID.randomUUID().toString();
        int randomTypeIndex = (int) Math.round(4 * Math.random());
        AccountType aType;
        switch (randomTypeIndex) {
            case 0:
                aType = AccountType.ASSET;
                break;
            case 1:
                aType = AccountType.EXPENSE;
                break;
            case 2:
                aType = AccountType.LIABILITY;
                break;
            default:
                aType = AccountType.INCOME;
        }
        return aLedger.createAccount(anAccountName, aType);
    }
}
