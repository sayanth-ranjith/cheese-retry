package com.sayanth_ranjith.cheese_retry_core.annotation;

import com.sayanth_ranjith.cheese_retry_core.core.predicate.type.RetryPredicateType;
import com.sayanth_ranjith.cheese_retry_core.core.strategy.type.BackoffStrategyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables automatic retry logic on methods.
 *
 * <p>Marks a method for retry handling with configurable attempts, backoff delays, and exception filtering.</p>
 *
 * <h2>Usage Example:</h2>
 * <pre>
 * &#64;CheeseRetry(
 *     maxAttempts = 3,
 *     timeoutInMillis = 5000,
 *     retryPredicateType = RetryPredicateType.TYPED_BASED_RETRY,
 *     retryOn = {IOException.class},
 *     backoffStrategyType = BackoffStrategyType.FIXED,
 *     delayInMillis = 500
 * )
 * public void callExternalService() {
 *     // This method will retry up to 3 times with 500ms delay between attempts
 *     // Only IOException will trigger retries; other exceptions fail immediately
 * }
 * </pre>
 *
 * @author Sayanth P V
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CheeseRetry {

    int maxAttempts() default 3;

    RetryPredicateType retryPredicateType() default RetryPredicateType.ALWAYS_RETRY;

    BackoffStrategyType backoffStrategyType() default BackoffStrategyType.FIXED;

    long delayInMillis() default 1000;

    long timeoutInMillis() default 0;

    Class<? extends Exception>[] retryOn() default {Exception.class};

}
