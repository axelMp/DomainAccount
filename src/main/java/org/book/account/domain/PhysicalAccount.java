package org.book.account.domain;

import org.apache.commons.lang3.Validate;

import java.util.LinkedList;
import java.util.List;

public class PhysicalAccount {
    PhysicalAccount(String name) {
        Validate.notNull(name, "The name must not be %s", null);
        Validate.notBlank(name, "Specify non-empty name for account");

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void contains(Account account) {
        Validate.notNull(account, "The account must not be %s", null);
        contains.add(account);
    }

    public void noLongerContains(Account account) {
        contains.remove(account);
    }

    private final String name;
    private final List<Account> contains = new LinkedList<Account>();
}
