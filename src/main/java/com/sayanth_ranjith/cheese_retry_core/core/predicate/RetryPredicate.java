package com.sayanth_ranjith.cheese_retry_core.core.predicate;

/**
 * Defines the contract for determining whether a failed operation should be retried
 * based on the exception that was thrown.
 *
 * <p>
 * Implementations of this interface encapsulate the logic for evaluating exceptions
 * and deciding if a retry attempt should be made. This allows for flexible and
 * customizable retry strategies based on specific exception types or conditions.
 * </p>
 *
 * <p>
 * Typical implementations might define sets of retryable exceptions, check exception
 * hierarchies, or evaluate exception properties to make retry decisions.
 * </p>
 *
 * @author Sayanth P V
 * @since 1.0
 */
public interface RetryPredicate {

    /**
     * Determines whether the given exception should trigger a retry attempt.
     *
     * <p>
     * This method evaluates the provided exception and returns {@code true} if the
     * operation that produced this exception should be retried, or {@code false} if
     * the retry policy should not attempt another execution.
     * </p>
     *
     * <p>
     * The decision logic is entirely implementation-specific and may consider factors such as:
     * <ul>
     *   <li>The type of the exception</li>
     *   <li>The exception hierarchy and inheritance chain</li>
     *   <li>Exception message or cause</li>
     *   <li>Other application-specific criteria</li>
     * </ul>
     * </p>
     *
     * @param exception the exception that occurred during execution (must not be null)
     * @return {@code true} if the operation should be retried, {@code false} otherwise
     */
    boolean shouldRetry(Exception exception);
}
