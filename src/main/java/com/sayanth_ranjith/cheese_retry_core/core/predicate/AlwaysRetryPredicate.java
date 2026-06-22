package com.sayanth_ranjith.cheese_retry_core.core.predicate;

public class AlwaysRetryPredicate implements RetryPredicate {

    @Override
    public boolean shouldRetry(Exception exception) {
        return true;
    }

}
