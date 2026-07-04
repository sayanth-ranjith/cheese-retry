package com.sayanth_ranjith.cheese_retry_core.aspect;

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
import com.sayanth_ranjith.cheese_retry_core.core.strategy.type.BackoffStrategyType;
import com.sayanth_ranjith.cheese_retry_core.executor.CoreRetryExecutor;
import com.sayanth_ranjith.cheese_retry_core.factory.RetryPolicyFactory;
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
        RetryPolicy retryPolicy = RetryPolicyFactory.buildPolicy(cheeseRetry);
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
}
