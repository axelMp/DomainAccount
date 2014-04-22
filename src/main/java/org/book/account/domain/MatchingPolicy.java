package org.book.account.domain;

public class MatchingPolicy {
    public boolean match(ITransaction transaction, IPlannedTransaction plannedTransaction) {
        boolean allowsForMatching = mayMatchWithIndividualTransaction(plannedTransaction);
        boolean sameNarration = transaction.getNarration().equals(plannedTransaction.getNarration());
        boolean sameTimeFrame = plannedTransaction.getSchedule().includes(transaction.getOccurredOn());
        return allowsForMatching && sameNarration && sameTimeFrame;
    }

    private boolean mayMatchWithIndividualTransaction(IPlannedTransaction plannedTransaction) {
        switch (plannedTransaction.getSchedule().getExecutionPolicy()) {
            case SINGLE:
                return true;
            case LINEARLY_PROGRESSING:
                return false;
            default:
                throw new IllegalArgumentException("unhandled policy " + plannedTransaction.getSchedule().getExecutionPolicy().toString());
        }
    }
}
