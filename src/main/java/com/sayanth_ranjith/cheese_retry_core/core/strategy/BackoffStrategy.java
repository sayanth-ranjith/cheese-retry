package com.sayanth_ranjith.cheese_retry_core.core.strategy;

/**
 * Defines the contract for calculating delays between retry attempts.
 *
 * <p>
 * A {@code BackoffStrategy} is responsible for determining how long the retry mechanism
 * should wait before attempting to retry a failed operation. This allows for flexible
 * retry timing strategies, such as:
 * <ul>
 *   <li><b>Fixed Backoff</b> - Wait a constant amount of time between retries</li>
 *   <li><b>Exponential Backoff</b> - Increase wait time exponentially with each retry</li>
 *   <li><b>Linear Backoff</b> - Increase wait time linearly with each retry</li>
 *   <li><b>Jittered Backoff</b> - Add randomization to prevent thundering herd</li>
 * </ul>
 * </p>
 *
 * <p>
 * Implementations of this interface encapsulate specific retry timing algorithms,
 * allowing applications to customize retry behavior based on their requirements.
 * </p>
 *
 * <h2>Usage:</h2>
 * <pre>
 * BackoffStrategy exponentialStrategy = new ExponentialBackoffStrategy();
 * long delayMillis = exponentialStrategy.nextDelayInMillis(3); // Get delay for 3rd attempt
 * Thread.sleep(delayMillis);
 * </pre>
 *
 * @author Sayanth P V
 * @since 1.0
 * @see com.sayanth_ranjith.cheese_retry_core.core.RetryPolicy
 */
public interface BackoffStrategy {

    /**
     * Calculates the delay in milliseconds before the next retry attempt.
     *
     * <p>
     * This method is called to determine how long the retry mechanism should wait
     * before attempting to retry a failed operation. The delay calculation can be
     * based on the attempt number, allowing for sophisticated backoff strategies.
     * </p>
     *
     * <p>
     * The implementation should consider:
     * <ul>
     *   <li>The attempt number to allow for progressive delay strategies</li>
     *   <li>Returning positive values to represent valid delays</li>
     *   <li>The maximum acceptable delay to prevent excessive waiting</li>
     *   <li>Potential overflow for very large attempt numbers</li>
     * </ul>
     * </p>
     *
     * <h3>Contract:</h3>
     * <ul>
     *   <li>The attempt parameter is 1-indexed (first retry is attempt 1)</li>
     *   <li>The return value must be non-negative (0 or more)</li>
     *   <li>The return value represents milliseconds</li>
     *   <li>Implementations should be consistent for the same attempt number</li>
     * </ul>
     *
     * <h3>Examples:</h3>
     * <pre>
     * // Fixed 1 second delay
     * nextDelayInMillis(1) → 1000
     * nextDelayInMillis(2) → 1000
     * nextDelayInMillis(3) → 1000
     *
     * // Exponential backoff: 100 * 2^n
     * nextDelayInMillis(1) → 200    (100 * 2^1)
     * nextDelayInMillis(2) → 400    (100 * 2^2)
     * nextDelayInMillis(3) → 800    (100 * 2^3)
     * </pre>
     *
     * @param attempt the retry attempt number (1-indexed, starting from 1 for the first retry)
     * @return the delay in milliseconds before the next retry attempt (must be >= 0)
     */
    long nextDelayInMillis(int attempt);
}
