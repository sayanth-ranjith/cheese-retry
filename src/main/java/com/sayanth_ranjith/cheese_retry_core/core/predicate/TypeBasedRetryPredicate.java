package com.sayanth_ranjith.cheese_retry_core.core.predicate;

import java.util.Set;

public class TypeBasedRetryPredicate implements RetryPredicate {

    private final Set<Class<? extends Exception>> retryableExceptions;

    public TypeBasedRetryPredicate(Set<Class<? extends Exception>> retryableExceptions) {
        this.retryableExceptions = retryableExceptions;
    }

    @Override
    public boolean shouldRetry(Exception exception) {
        return retryableExceptions.stream()
                .anyMatch(clazz -> clazz.isInstance(exception));
    }

}
