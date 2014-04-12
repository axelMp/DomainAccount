package org.book.account.domain;

import java.util.LinkedList;
import java.util.List;

class AccountHierarchy {
    void add(Account account) {
        if ( null == account ) {
            throw new IllegalArgumentException("account cannot be null");
        }

        for (Account anAccount:accounts) {
            if ( anAccount.getName().equals(account.getName())) {
                throw new IllegalArgumentException("account with name "+account.getName()+ " already exists");
            }
        }

        accounts.add(account);
    }

    void remove(Account account) {
        if ( null == account ) {
            throw new IllegalArgumentException("account cannot be null");
        }
        accounts.remove(account);
    }

    void add(PhysicalAccount account) {
        if ( null == account ) {
            throw new IllegalArgumentException("physical account cannot be null");
        }

        for (PhysicalAccount anAccount:physicalAccounts) {
            if ( anAccount.getName().equals(account.getName())) {
                throw new IllegalArgumentException("physical account with name "+account.getName()+ " already exists");
            }
        }

        physicalAccounts.add(account);
    }

    void remove(PhysicalAccount account) {
        if ( null == account ) {
            throw new IllegalArgumentException("account cannot be null");
        }
        physicalAccounts.remove(account);
    }

    void assertThatAccountExists(Account anAccount) {
        if ( ! accounts.contains(anAccount)) {
            throw new IllegalArgumentException("Account "+anAccount.getName()+" does not exist in this account system");
        }
    }

    private final List<Account> accounts = new LinkedList<Account>();
    private final List<PhysicalAccount> physicalAccounts = new LinkedList<PhysicalAccount>();
}
