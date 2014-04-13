package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Account {

    private final String name;
    private final AccountType accountType;
    private final List<Transaction> transactions = new LinkedList<Transaction>();

    Account(String name, AccountType accountType) {
        Validate.notNull(name, "The name must not be null");
        Validate.notBlank(name, "Specify non-empty name for account");

        this.name = name;
        this.accountType = accountType;
    }

    public Amount closure(Date date) {
        Amount result = Amount.noAmount();
        for (Transaction aTransaction : transactions) {
            if (aTransaction.isPlanned()) {
                continue;
            }

            if (aTransaction.getFrom().equals(this)) {
                result = Amount.subtract(result, aTransaction.valueAt(date));
            } else {
                result = Amount.add(result, aTransaction.valueAt(date));
            }
        }
        return result;
    }

    void add(Transaction aTransaction) {
        transactions.add(aTransaction);
    }

    void remove(Transaction aTransaction) {
        transactions.remove(aTransaction);
    }

    public String getName() {
        return name;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public enum AccountType {
        INCOME,
        EXPENSE,
        SHORT_TERM_LIABILITY,
        LONG_TERM_LIABILITY,
        SHORT_TERM_ASSET,
        LONG_TERM_ASSET
    }
}
