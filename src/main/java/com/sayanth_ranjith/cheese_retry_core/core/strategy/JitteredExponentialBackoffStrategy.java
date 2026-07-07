package com.sayanth_ranjith.cheese_retry_core.core.strategy;

import com.sayanth_ranjith.cheese_retry_core.core.exception.CheeseRetryConfigurationException;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Exponential backoff with jitter.
 *
 * <p>
 * This strategy calculates the same exponential base delay as {@link ExponentialBackoffStrategy},
 * then randomizes the actual sleep time between {@code 0} and that base delay (inclusive).
 * This reduces the chance that many callers retry in lockstep.
 * @author Sayanth P V
 * </p>
 */
public class JitteredExponentialBackoffStrategy implements BackoffStrategy {

    private final long exponentialDelayInMillis;

    public JitteredExponentialBackoffStrategy(long exponentialDelayInMillis) {
        if (exponentialDelayInMillis <= 0) {
            throw new CheeseRetryConfigurationException("Exponential backoff strategy must be non negative");
        }
        this.exponentialDelayInMillis = exponentialDelayInMillis;
    }

    @Override
    public long nextDelayInMillis(int attempt) {
        long baseDelay = safeExponentialDelay(attempt);
        if (baseDelay == Long.MAX_VALUE) {
            return ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        }
        return ThreadLocalRandom.current().nextLong(baseDelay + 1);
    }

    private long safeExponentialDelay(int attempt) {
        if (attempt <= 0) {
            throw new IllegalArgumentException("Attempt must be greater than 0");
        }

        if (attempt >= 63) {
            return Long.MAX_VALUE;
        }

        long multiplier = 1L << attempt;

        long maxBaseDelay = Long.MAX_VALUE / multiplier;
        if (exponentialDelayInMillis > maxBaseDelay) {
            return Long.MAX_VALUE;
        }

        return exponentialDelayInMillis * multiplier;
    }
}
