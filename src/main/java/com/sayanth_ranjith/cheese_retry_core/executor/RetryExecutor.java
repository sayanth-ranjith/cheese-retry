package com.sayanth_ranjith.cheese_retry_core.executor;

public interface RetryExecutor {
    <T> T execute(CheeseRetryCallable<T> cheeseRetryCallable) throws Exception;
}
