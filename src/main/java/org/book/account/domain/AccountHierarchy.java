package org.book.account.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.LinkedList;
import java.util.List;

@Entity
class AccountHierarchy {
    private static final Logger logger = LogManager.getLogger(Account.class.getName());
    private final List<Account> accounts = new LinkedList<Account>();
    private final List<PhysicalAccount> physicalAccounts = new LinkedList<PhysicalAccount>();
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    void add(Account account) {
        for (Account anAccount : accounts) {
            if ( anAccount.getName().equals(account.getName())) {
                throw new IllegalArgumentException("account with name " + account.getName() + " already exists");
            }
        }

        accounts.add(account);
    }

    void remove(Account account) {
        accounts.remove(account);
    }

    void add(PhysicalAccount account) {
        for (PhysicalAccount anAccount : physicalAccounts) {
            if (anAccount.getName().equals(account.getName())) {
                throw new IllegalArgumentException("physical account with name " + account.getName() + " already exists");
            }
        }

        physicalAccounts.add(account);
    }

    void remove(PhysicalAccount account) {
        physicalAccounts.remove(account);
    }

    void assertThatAccountExists(Account anAccount) {
        if (!accounts.contains(anAccount)) {
            if (logger.isErrorEnabled()) {
                logger.error("account " + anAccount.getName() + " unknown");

                StringBuilder builder = new StringBuilder();
                for (Account account : accounts) {
                    builder.append(account.getName());
                    builder.append(" ");
                }
                logger.error("known accounts are " + builder.toString());
            }

            throw new IllegalArgumentException("Account " + anAccount.getName() + " does not exist");
        }
    }
}
