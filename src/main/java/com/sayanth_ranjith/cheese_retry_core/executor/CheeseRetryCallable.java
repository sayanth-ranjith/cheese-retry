package com.sayanth_ranjith.cheese_retry_core.executor;

@FunctionalInterface
public interface CheeseRetryCallable<T> {
    T call() throws Exception;
}
