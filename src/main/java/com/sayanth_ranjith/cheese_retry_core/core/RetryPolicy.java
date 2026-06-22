package com.sayanth_ranjith.cheese_retry_core.core;

import com.sayanth_ranjith.cheese_retry_core.core.exception.CheeseRetryConfigurationException;
import com.sayanth_ranjith.cheese_retry_core.core.exception.ExceptionConstants;
import com.sayanth_ranjith.cheese_retry_core.core.predicate.BackoffStrategy;
import com.sayanth_ranjith.cheese_retry_core.core.predicate.RetryPredicate;
import lombok.Builder;


public class RetryPolicy {

    private final int maxAttempts;
    private final BackoffStrategy backoffStrategy;
    private final RetryPredicate retryPredicate;

    @Builder
    public RetryPolicy(int maxAttempts, BackoffStrategy backoffStrategy, RetryPredicate retryPredicate) {
        this.maxAttempts = maxAttempts;
        this.backoffStrategy = backoffStrategy;
        this.retryPredicate = retryPredicate;
        sanitize();
    }

    private void sanitize() {
        if  (maxAttempts <= 0) {
            throw new CheeseRetryConfigurationException(ExceptionConstants.MAX_ATTEMPS_LESS_THAN_EQUAL_TO_ZERO);
        }
        if (backoffStrategy == null) {
            throw new CheeseRetryConfigurationException(ExceptionConstants.BACK_OFF_STRATEGY_NULL);
        }
        if (retryPredicate == null) {
            throw new CheeseRetryConfigurationException(ExceptionConstants.RETRY_PREDICATE_NULL);
        }
    }
}
