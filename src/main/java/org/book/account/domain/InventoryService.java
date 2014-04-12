package org.book.account.domain;


public class InventoryService {
    /**
     * Adds transactions such that sum of account closures for all accounts attached to the physical account
     * equals currentlyInPhysicalAccount
     * @param account account to take stock of
     * @param currentlyInAccount current value of physical account
     * @param inventoryAccount account to book any difference between current value and book value against
     */
    public void takeStock(PhysicalAccount account, Amount currentlyInAccount, Account inventoryAccount) {
        // TODO
    }
}
