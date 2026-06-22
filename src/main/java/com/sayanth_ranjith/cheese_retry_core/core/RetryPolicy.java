package com.sayanth_ranjith.cheese_retry_core.core;

import com.sayanth_ranjith.cheese_retry_core.core.exception.CheeseRetryConfigurationException;
import com.sayanth_ranjith.cheese_retry_core.core.exception.ExceptionConstants;
import com.sayanth_ranjith.cheese_retry_core.core.predicate.RetryPredicate;
import com.sayanth_ranjith.cheese_retry_core.core.strategy.BackoffStrategy;
import lombok.Builder;
import lombok.Getter;

/**
 * Defines a retry policy that governs how and when retry attempts should be made.
 *
 * <p>
 * A {@code RetryPolicy} encapsulates the configuration for executing operations with retry logic.
 * It consists of three key components:
 * <ul>
 *   <li><b>Max Attempts</b> - The maximum number of times an operation can be retried</li>
 *   <li><b>Backoff Strategy</b> - The algorithm for calculating delays between retry attempts</li>
 *   <li><b>Retry Predicate</b> - The logic for determining which exceptions should trigger a retry</li>
 * </ul>
 * </p>
 *
 * <p>
 * Instances must be created using the builder pattern to ensure validation is always performed.
 * All parameters are validated upon object creation, and invalid configurations will throw
 * a {@link CheeseRetryConfigurationException}.
 * </p>
 *
 * <h2>Usage Example:</h2>
 * <pre>
 * RetryPolicy policy = RetryPolicy.builder()
 *     .maxAttempts(3)
 *     .backoffStrategy(new ExponentialBackoff())
 *     .retryPredicate(new DefaultRetryPredicate(exceptionSet))
 *     .build();
 * </pre>
 *
 * @author Sayanth P V
 * @since 1.0
 */
@Builder
@Getter
public class RetryPolicy {

    /**
     * The maximum number of retry attempts allowed.
     * Must be greater than 0. Used to limit the number of retries
     * to prevent infinite retry loops.
     */
    private final int maxAttempts;

    /**
     * The backoff strategy for calculating delays between retry attempts.
     * Determines how long the system waits before attempting a retry.
     * Must not be null.
     */
    private final BackoffStrategy backoffStrategy;

    /**
     * The retry predicate for determining which exceptions should trigger a retry.
     * This logic decides whether an operation should be retried based on the exception
     * that was thrown. Must not be null.
     */
    private final RetryPredicate retryPredicate;

    /**
     * Package-private constructor to enforce builder usage and ensure validation.
     * This constructor is not intended to be called directly from outside the package.
     * Use {@link #builder()} to create instances using the builder pattern.
     *
     * <p>
     * Upon instantiation, all parameters are validated to ensure a valid retry policy configuration:
     * <ul>
     *   <li>{@code maxAttempts} must be greater than 0</li>
     *   <li>{@code backoffStrategy} must not be null</li>
     *   <li>{@code retryPredicate} must not be null</li>
     * </ul>
     * </p>
     *
     * @param maxAttempts the maximum number of retry attempts (must be > 0)
     * @param backoffStrategy the strategy for calculating delays between retries (must not be null)
     * @param retryPredicate the logic to determine if an exception should trigger a retry (must not be null)
     * @throws CheeseRetryConfigurationException if any validation check fails with a descriptive error message
     */
    RetryPolicy(int maxAttempts, BackoffStrategy backoffStrategy, RetryPredicate retryPredicate) {
        this.maxAttempts = maxAttempts;
        this.backoffStrategy = backoffStrategy;
        this.retryPredicate = retryPredicate;
        validate();
    }

    /**
     * Validates all components of this retry policy to ensure they meet the required constraints.
     *
     * <p>
     * This method is automatically called by the constructor to validate the policy parameters
     * before the object is fully initialized. It performs the following checks:
     * <ul>
     *   <li>Verifies {@code maxAttempts} is greater than 0 to prevent invalid configurations</li>
     *   <li>Verifies {@code backoffStrategy} is not null to avoid NullPointerException at runtime</li>
     *   <li>Verifies {@code retryPredicate} is not null to avoid NullPointerException at runtime</li>
     * </ul>
     * </p>
     *
     * @throws CheeseRetryConfigurationException if {@code maxAttempts} is less than or equal to 0
     * @throws CheeseRetryConfigurationException if {@code backoffStrategy} is null
     * @throws CheeseRetryConfigurationException if {@code retryPredicate} is null
     */
    private void validate() {
        if (maxAttempts <= 0) {
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