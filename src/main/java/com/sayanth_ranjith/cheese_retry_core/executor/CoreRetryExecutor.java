package com.sayanth_ranjith.cheese_retry_core.executor;

import com.sayanth_ranjith.cheese_retry_core.core.policy.RetryPolicy;
import lombok.Builder;

@Builder
public class CoreRetryExecutor implements RetryExecutor {

    private final RetryPolicy retryPolicy;

    public CoreRetryExecutor(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    @Override
    public <T> T execute(CheeseRetryCallable<T> cheeseRetryCallable) throws Exception {
        int attempt = 1;
        while (true) {
            try {
                return cheeseRetryCallable.call();
            }
            catch (Exception ex) {
                if (attempt >= retryPolicy.getMaxAttempts()) {
                    throw ex;
                }
                if (!retryPolicy.getRetryPredicate().shouldRetry(ex)) {
                    throw ex;
                }
                Thread.sleep(retryPolicy.getBackoffStrategy().nextDelayInMillis(attempt));
                attempt++;
            }
        }
    }

}

