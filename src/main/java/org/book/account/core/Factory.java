package org.book.account.core;

import org.book.account.domain.IFactory;
import org.book.account.domain.ILedger;

public class Factory implements IFactory {
    @Override
    public ILedger createLedger(String name) {
        return new Ledger(name);
    }
}
