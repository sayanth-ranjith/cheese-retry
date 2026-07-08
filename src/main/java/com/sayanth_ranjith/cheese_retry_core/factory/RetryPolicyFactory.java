package com.sayanth_ranjith.cheese_retry_core.factory;

import com.sayanth_ranjith.cheese_retry_core.annotation.CheeseRetry;
import com.sayanth_ranjith.cheese_retry_core.core.exception.CheeseRetryConfigurationException;
import com.sayanth_ranjith.cheese_retry_core.core.policy.RetryPolicy;
import com.sayanth_ranjith.cheese_retry_core.core.predicate.AlwaysRetryPredicate;
import com.sayanth_ranjith.cheese_retry_core.core.predicate.NeverRetryPredicate;
import com.sayanth_ranjith.cheese_retry_core.core.predicate.RetryPredicate;
import com.sayanth_ranjith.cheese_retry_core.core.predicate.TypeBasedRetryPredicate;
import com.sayanth_ranjith.cheese_retry_core.core.predicate.type.RetryPredicateType;
import com.sayanth_ranjith.cheese_retry_core.core.strategy.BackoffStrategy;
import com.sayanth_ranjith.cheese_retry_core.core.strategy.ExponentialBackoffStrategy;
import com.sayanth_ranjith.cheese_retry_core.core.strategy.FixedBackoffStrategy;
import com.sayanth_ranjith.cheese_retry_core.core.strategy.JitteredExponentialBackoffStrategy;
import com.sayanth_ranjith.cheese_retry_core.core.strategy.type.BackoffStrategyType;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Factory for building {@link RetryPolicy} instances from the {@link CheeseRetry}
 * annotation metadata.
 *
 * <p>This class centralizes retry-policy construction so the aspect layer can
 * remain focused on interception and delegation.
 *
 * @author Sayanth P V
 */
public class RetryPolicyFactory {

    /**
     * Builds a retry policy from the supplied {@link CheeseRetry} annotation.
     *
     * <p>The generated policy includes the configured maximum attempts, backoff
     * strategy, and retry predicate.
     *
     * @param cheeseRetry the annotation carrying retry configuration
     * @return a fully constructed retry policy
     * @throws CheeseRetryConfigurationException if the annotation contains an
     *                                           unsupported or missing
     *                                           configuration value
     */
    public static RetryPolicy buildPolicy(CheeseRetry cheeseRetry) {
        return RetryPolicy.builder()
                .maxAttempts(cheeseRetry.maxAttempts())
                .backoffStrategy(buildBackoffStrategy(cheeseRetry.backoffStrategyType(), cheeseRetry.delayInMillis()))
                .retryPredicate(buildRetryPredicate(cheeseRetry.retryPredicateType(), cheeseRetry.retryOn()))
                .timeoutInMillis(cheeseRetry.timeoutInMillis())
                .build();
    }

    private static BackoffStrategy buildBackoffStrategy(BackoffStrategyType strategyType, long delayInMillis) {
        if (strategyType == null) {
            throw new CheeseRetryConfigurationException("Backoff strategy type cannot be null");
        }

        return switch (strategyType) {
            case FIXED -> new FixedBackoffStrategy(delayInMillis);
            case EXPONENTIAL -> new ExponentialBackoffStrategy(delayInMillis);
            case EXPONENTIAL_WITH_JITTER -> new JitteredExponentialBackoffStrategy(delayInMillis);
        };
    }

    private static RetryPredicate buildRetryPredicate(RetryPredicateType predicateType, Class<? extends Exception>[] retryOn) {
        if (predicateType == null) {
            throw new CheeseRetryConfigurationException("Retry predicate type cannot be null");
        }

        return switch (predicateType) {
            case ALWAYS_RETRY -> new AlwaysRetryPredicate();
            case NEVER_RETRY -> new NeverRetryPredicate();
            case TYPED_BASED_RETRY -> buildTypeBasedRetryPredicate(retryOn);
        };
    }

    private static RetryPredicate buildTypeBasedRetryPredicate(Class<? extends Exception>[] retryOn) {
        if (retryOn == null || retryOn.length == 0) {
            throw new CheeseRetryConfigurationException("retryOn must contain at least one exception type for type-based retry");
        }

        Set<Class<? extends Exception>> retryableExceptions = Arrays.stream(retryOn).collect(Collectors.toUnmodifiableSet());

        return new TypeBasedRetryPredicate(retryableExceptions);
    }

}
