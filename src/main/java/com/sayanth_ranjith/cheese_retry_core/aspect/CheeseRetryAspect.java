//package com.sayanth_ranjith.cheese_retry_core.aspect;
//
//import com.sayanth_ranjith.cheese_retry_core.annotation.CheeseRetry;
//import com.sayanth_ranjith.cheese_retry_core.core.strategy.FixedBackoffStrategy;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * AspectJ aspect for handling the @CheeseRetry annotation.
// * Intercepts method calls and applies retry logic with backoff.
// *
// * @author Sayanth P V
// * @since 1.0
// */
//@Aspect
//@Component
//@Slf4j
//public class CheeseRetryAspect {
//
//    /**
//     * Intercepts methods annotated with @CheeseRetry and applies retry logic.
//     *
//     * @param joinPoint the method execution join point
//     * @param cheeseRetry the @CheeseRetry annotation
//     * @return the return value of the method
//     * @throws Throwable if all retries are exhausted
//     */
//    @Around("@annotation(cheeseRetry)")
//    public Object handleCheeseRetry(ProceedingJoinPoint joinPoint, CheeseRetry cheeseRetry) throws Throwable {
//        String methodName = joinPoint.getSignature().getName();
//        int maxAttempts = cheeseRetry.maxAttempts();
//        long backoffMillis = cheeseRetry.backoffMillis();
//        Class<? extends Throwable>[] retryOn = cheeseRetry.retryOn();
//        Class<? extends Throwable>[] ignore = cheeseRetry.ignore();
//
//        Set<Class<? extends Throwable>> retryOnSet = new HashSet<>(Arrays.asList(retryOn));
//        Set<Class<? extends Throwable>> ignoreSet = new HashSet<>(Arrays.asList(ignore));
//
//        int attempt = 0;
//        Throwable lastException = null;
//
//        while (attempt < maxAttempts) {
//            attempt++;
//            try {
//                log.debug("Executing method '{}' - Attempt {}/{}", methodName, attempt, maxAttempts);
//                return joinPoint.proceed();
//            } catch (Throwable e) {
//                lastException = e;
//
//                // Check if exception should be ignored (never retry)
//                if (ignoreSet.stream().anyMatch(exc -> exc.isInstance(e))) {
//                    log.warn("Exception '{}' is in ignore list for method '{}'. Not retrying.",
//                            e.getClass().getSimpleName(), methodName);
//                    throw e;
//                }
//
//                // Check if exception should trigger retry
//                boolean shouldRetry = retryOnSet.stream().anyMatch(exc -> exc.isInstance(e));
//
//                if (!shouldRetry) {
//                    log.warn("Exception '{}' is not in retryOn list for method '{}'. Not retrying.",
//                            e.getClass().getSimpleName(), methodName);
//                    throw e;
//                }
//
//                // If this is the last attempt, throw the exception
//                if (attempt >= maxAttempts) {
//                    log.error("Method '{}' failed after {} attempts. Exception: {}",
//                            methodName, maxAttempts, e.getMessage());
//                    throw e;
//                }
//
//                // Wait before retrying
//                log.warn("Method '{}' failed on attempt {} with '{}'. Retrying after {}ms...",
//                        methodName, attempt, e.getClass().getSimpleName(), backoffMillis);
//                Thread.sleep(backoffMillis);
//            }
//        }
//
//        // This shouldn't be reached, but throw if it does
//        if (lastException != null) {
//            throw lastException;
//        }
//
//        throw new IllegalStateException("Unexpected state in CheeseRetryAspect");
//    }
//}