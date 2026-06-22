package com.sayanth_ranjith.cheese_retry_core.core.strategy;

public class ExponentialBackoffStrategy implements BackoffStrategy{

    private final long exponentialDelayInMillis;

    public ExponentialBackoffStrategy(long exponentialDelayInMillis) {
        this.exponentialDelayInMillis = exponentialDelayInMillis;
    }

    @Override
    public long nextDelayInMillis(int attempt) {
        return exponentialDelayInMillis * (1L << attempt);
    }
}
