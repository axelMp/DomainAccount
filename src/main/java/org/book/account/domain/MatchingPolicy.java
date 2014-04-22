package org.book.account.domain;

public class MatchingPolicy {
    public boolean match(ITransaction transaction, IPlannedTransaction plannedTransaction) {
        boolean allowsForMatching = plannedTransaction.getSchedule().mayMatchWithIndividualTransaction();
        boolean sameNarration = transaction.getNarration().equals(plannedTransaction.getNarration());
        boolean sameTimeFrame = plannedTransaction.getSchedule().includes(transaction.getOccurredOn());
        return allowsForMatching && sameNarration && sameTimeFrame;
    }
}
