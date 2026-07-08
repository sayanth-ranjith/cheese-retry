package com.sayanth_ranjith.cheese_retry_core.executor;

import com.sayanth_ranjith.cheese_retry_core.core.exception.CheeseRetryExecutorException;
import com.sayanth_ranjith.cheese_retry_core.core.exception.ExceptionConstants;
import com.sayanth_ranjith.cheese_retry_core.core.policy.RetryPolicy;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

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
        long deadlineNanos = hasTimeout()
                ? System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(retryPolicy.getTimeoutInMillis())
                : Long.MAX_VALUE;

        while (true) {
            try {
                ensureNotInterrupted();
                ensureNotTimedOut(deadlineNanos);
                return cheeseRetryCallable.call();
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new CheeseRetryExecutorException(ExceptionConstants.EXECUTION_INTERRUPTED, ex);
            }
            catch (CheeseRetryExecutorException ex) {
                throw ex;
            }
            catch (Exception ex) {
                log.info("[ATTEMPT:] " + attempt + " FAILED, CHEESE IS RETRYING...");
                if (attempt >= retryPolicy.getMaxAttempts()) {
                    throw new CheeseRetryExecutorException(ExceptionConstants.EXECPTION_ATTEMPTS_REACHED, ex);
                }
                if (!retryPolicy.getRetryPredicate().shouldRetry(ex)) {
                    throw new CheeseRetryExecutorException(ExceptionConstants.EXCEPTION_RETRY_PREDICATE, ex);
                }
                try {
                    sleepUntilNextAttempt(attempt, deadlineNanos, ex);
                }
                catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new CheeseRetryExecutorException(ExceptionConstants.EXECUTION_INTERRUPTED, interruptedException);
                }
                attempt++;
            }
        }
    }

    private boolean hasTimeout() {
        return retryPolicy.getTimeoutInMillis() > 0;
    }

    private void ensureNotInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException(ExceptionConstants.EXECUTION_INTERRUPTED);
        }
    }

    private void ensureNotTimedOut(long deadlineNanos) {
        if (deadlineNanos != Long.MAX_VALUE && System.nanoTime() >= deadlineNanos) {
            throw new CheeseRetryExecutorException(ExceptionConstants.EXECUTION_TIMEOUT_REACHED);
        }
    }

    private void sleepUntilNextAttempt(int attempt, long deadlineNanos, Exception lastFailure) throws InterruptedException {
        long nextDelay = retryPolicy.getBackoffStrategy().nextDelayInMillis(attempt);
        if (deadlineNanos == Long.MAX_VALUE) {
            Thread.sleep(nextDelay);
            return;
        }

        long remainingNanos = deadlineNanos - System.nanoTime();
        if (remainingNanos <= 0) {
            throw new CheeseRetryExecutorException(ExceptionConstants.EXECUTION_TIMEOUT_REACHED, lastFailure);
        }

        long remainingMillis = TimeUnit.NANOSECONDS.toMillis(remainingNanos);
        if (remainingMillis <= 0) {
            throw new CheeseRetryExecutorException(ExceptionConstants.EXECUTION_TIMEOUT_REACHED, lastFailure);
        }

        Thread.sleep(Math.min(nextDelay, remainingMillis));
    }

}

