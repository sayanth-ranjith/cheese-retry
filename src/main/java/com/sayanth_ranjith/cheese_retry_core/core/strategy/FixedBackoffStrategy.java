package com.sayanth_ranjith.cheese_retry_core.core.strategy;

import com.sayanth_ranjith.cheese_retry_core.core.exception.CheeseRetryConfigurationException;

public class FixedBackoffStrategy implements BackoffStrategy {

    private final long fixedDelayInMillis;

    public FixedBackoffStrategy(long fixedDelayInMillis) {
        if (fixedDelayInMillis <= 0) {
            throw new CheeseRetryConfigurationException("Fixed delay must be non-negative");
        }
        this.fixedDelayInMillis = fixedDelayInMillis;
    }

    @Override
    public long nextDelayInMillis(int attempt) {
        return fixedDelayInMillis;
    }
}
