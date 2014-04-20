package org.book.account.core;

import org.apache.commons.lang3.Validate;

import java.util.LinkedList;
import java.util.List;

public class PhysicalAccount {
    private final String name;
    private final List<Account> contains = new LinkedList<Account>();

    PhysicalAccount(String name) {
        Validate.notNull(name, "The name must not be null");
        Validate.notBlank(name, "Specify non-empty name for account");

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void contains(Account account) {
        Validate.notNull(account, "The account must not be null");
        contains.add(account);
    }

    public void noLongerContains(Account account) {
        contains.remove(account);
    }
}
