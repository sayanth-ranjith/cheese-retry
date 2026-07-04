package com.sayanth_ranjith.cheese_retry_core.executor;

import com.sayanth_ranjith.cheese_retry_core.core.exception.CheeseRetryExecutorException;
import com.sayanth_ranjith.cheese_retry_core.core.exception.ExceptionConstants;
import com.sayanth_ranjith.cheese_retry_core.core.policy.RetryPolicy;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
                log.info("[ATTEMPT:] " + attempt + " FAILED, CHEESE IS RETRYING...");
                if (attempt >= retryPolicy.getMaxAttempts()) {
                    throw new CheeseRetryExecutorException(ExceptionConstants.EXECPTION_ATTEMPTS_REACHED, ex);
                }
                if (!retryPolicy.getRetryPredicate().shouldRetry(ex)) {
                    throw new CheeseRetryExecutorException(ExceptionConstants.EXCEPTION_RETRY_PREDICATE, ex);
                }
                Thread.sleep(retryPolicy.getBackoffStrategy().nextDelayInMillis(attempt));
                attempt++;
            }
        }
    }

}

