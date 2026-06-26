package com.sayanth_ranjith.cheese_retry_core.aspect;

import com.sayanth_ranjith.cheese_retry_core.annotation.CheeseRetry;
import com.sayanth_ranjith.cheese_retry_core.core.exception.CheeseRetryConfigurationException;
import com.sayanth_ranjith.cheese_retry_core.core.exception.CheeseRetryExecutorException;
import com.sayanth_ranjith.cheese_retry_core.core.policy.RetryPolicy;
import com.sayanth_ranjith.cheese_retry_core.core.predicate.AlwaysRetryPredicate;
import com.sayanth_ranjith.cheese_retry_core.core.predicate.NeverRetryPredicate;
import com.sayanth_ranjith.cheese_retry_core.core.predicate.RetryPredicate;
import com.sayanth_ranjith.cheese_retry_core.core.predicate.TypeBasedRetryPredicate;
import com.sayanth_ranjith.cheese_retry_core.core.predicate.type.RetryPredicateType;
import com.sayanth_ranjith.cheese_retry_core.core.strategy.BackoffStrategy;
import com.sayanth_ranjith.cheese_retry_core.core.strategy.ExponentialBackoffStrategy;
import com.sayanth_ranjith.cheese_retry_core.core.strategy.FixedBackoffStrategy;
import com.sayanth_ranjith.cheese_retry_core.core.strategy.type.BackoffStrategyType;
import com.sayanth_ranjith.cheese_retry_core.executor.CoreRetryExecutor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
public class CheeseAspect {

    @Around("@annotation(cheeseRetry)")
    public Object resolveCheeseRetry(ProceedingJoinPoint pjp, CheeseRetry cheeseRetry) throws Throwable {
        RetryPolicy retryPolicy = buildPolicy(cheeseRetry);
        CoreRetryExecutor executor = CoreRetryExecutor.builder()
                .retryPolicy(retryPolicy)
                .build();

        try {
            return executor.execute(() -> {
                try {
                    return pjp.proceed();
                }
                catch (Exception ex) {
                    throw ex;
                }
                catch (Throwable throwable) {
                    throw new RuntimeException("Unexpected issue during Aspect resolution for cheese.", throwable);
                }
            });
        }
        catch (Exception ex) {
            throw ex;
        }
    }

    private RetryPolicy buildPolicy(CheeseRetry cheeseRetry) {
        return RetryPolicy.builder()
                .maxAttempts(cheeseRetry.maxAttempts())
                .backoffStrategy(buildBackoffStrategy(cheeseRetry.backoffStrategyType(), cheeseRetry.delayInMillis()))
                .retryPredicate(buildRetryPredicate(cheeseRetry.retryPredicateType(), cheeseRetry.retryOn()))
                .build();
    }

    private BackoffStrategy buildBackoffStrategy(BackoffStrategyType strategyType, long delayInMillis) {
        if (strategyType == null) {
            throw new CheeseRetryConfigurationException("Backoff strategy type cannot be null");
        }

        return switch (strategyType) {
            case FIXED -> new FixedBackoffStrategy(delayInMillis);
            case EXPONENTIAL -> new ExponentialBackoffStrategy(delayInMillis);
        };
    }

    private RetryPredicate buildRetryPredicate(RetryPredicateType predicateType, Class<? extends Exception>[] retryOn) {
        if (predicateType == null) {
            throw new CheeseRetryConfigurationException("Retry predicate type cannot be null");
        }

        return switch (predicateType) {
            case ALWAYS_RETRY -> new AlwaysRetryPredicate();
            case NEVER_RETRY -> new NeverRetryPredicate();
            case TYPED_BASED_RETRY -> buildTypeBasedRetryPredicate(retryOn);
        };
    }

    private RetryPredicate buildTypeBasedRetryPredicate(Class<? extends Exception>[] retryOn) {
        if (retryOn == null || retryOn.length == 0) {
            throw new CheeseRetryConfigurationException("retryOn must contain at least one exception type for type-based retry");
        }

        Set<Class<? extends Exception>> retryableExceptions = Arrays.stream(retryOn).collect(Collectors.toUnmodifiableSet());

        return new TypeBasedRetryPredicate(retryableExceptions);
    }

}
