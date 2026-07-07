package com.sayanth_ranjith.cheese_retry_core.core.predicate;

/**
 * @deprecated Prefer not applying {@code @CheeseRetry} at all, or use
 *             {@link AlwaysRetryPredicate} / {@link TypeBasedRetryPredicate}
 *             to describe explicit retry behavior.
 */
@Deprecated(forRemoval = false)
public class NeverRetryPredicate implements RetryPredicate {

    @Override
    public boolean shouldRetry(Exception exception) {
        return false;
    }

}
