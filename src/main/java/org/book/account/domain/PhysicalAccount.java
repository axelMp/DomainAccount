package org.book.account.domain;

import java.util.LinkedList;
import java.util.List;

public class PhysicalAccount {
    PhysicalAccount(String name) {
        if (null == name) {
            throw new IllegalArgumentException("Specify non-null name for account");
        }

        if ( "".equals(name) ) {
            throw new IllegalArgumentException("Specify non-empty name for account");
        }

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void contains(Account account) {
        if (null == account) {
            throw new IllegalArgumentException("Specify non-null for account");
        }
        contains.add(account);
    }

    public void noLongerContains(Account account) {
        contains.remove(account);
    }

    private final String name;
    private final List<Account> contains = new LinkedList<Account>();
}
