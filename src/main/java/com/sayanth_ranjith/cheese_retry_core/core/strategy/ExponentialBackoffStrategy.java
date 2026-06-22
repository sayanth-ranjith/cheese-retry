package com.sayanth_ranjith.cheese_retry_core.core.strategy;

import com.sayanth_ranjith.cheese_retry_core.core.exception.CheeseRetryConfigurationException;

public class ExponentialBackoffStrategy implements BackoffStrategy{

    private final long exponentialDelayInMillis;

    public ExponentialBackoffStrategy(long exponentialDelayInMillis) {
        if (exponentialDelayInMillis <= 0) {
            throw new CheeseRetryConfigurationException("Exponential backoff strategy must be non negative");
        }
        this.exponentialDelayInMillis = exponentialDelayInMillis;
    }

    @Override
    public long nextDelayInMillis(int attempt) {
        return exponentialDelayInMillis * (1L << attempt);
    }
}
