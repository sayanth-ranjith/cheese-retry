package com.sayanth_ranjith.cheese_retry_core.executor;

import com.sayanth_ranjith.cheese_retry_core.core.exception.CheeseRetryExecutorException;
import com.sayanth_ranjith.cheese_retry_core.core.exception.ExceptionConstants;
import com.sayanth_ranjith.cheese_retry_core.core.policy.RetryPolicy;
import com.sayanth_ranjith.cheese_retry_core.core.predicate.AlwaysRetryPredicate;
import com.sayanth_ranjith.cheese_retry_core.core.strategy.FixedBackoffStrategy;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoreRetryExecutorTest {

    @Test
    void interruptedBackoffStopsRetryingAndRestoresInterruptStatus() throws Exception {
        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxAttempts(3)
                .timeoutInMillis(0)
                .backoffStrategy(new FixedBackoffStrategy(1_000))
                .retryPredicate(new AlwaysRetryPredicate())
                .build();

        CoreRetryExecutor executor = new CoreRetryExecutor(retryPolicy);
        CountDownLatch firstAttemptReached = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        AtomicInteger attempts = new AtomicInteger();

        Thread worker = new Thread(() -> {
            try {
                executor.execute(() -> {
                    attempts.incrementAndGet();
                    firstAttemptReached.countDown();
                    throw new Exception("boom");
                });
            }
            catch (Throwable throwable) {
                failure.set(throwable);
            }
        });

        worker.start();
        assertTrue(firstAttemptReached.await(1, TimeUnit.SECONDS), "The first retry attempt never started");

        worker.interrupt();
        worker.join(2_000);

        assertFalse(worker.isAlive(), "Worker thread should have stopped after interruption");
        assertEquals(1, attempts.get(), "Interrupted backoff should stop before a second attempt");

        Throwable thrown = failure.get();
        assertNotNull(thrown, "Expected the worker to capture a failure");
        assertInstanceOf(CheeseRetryExecutorException.class, thrown);
        assertEquals(ExceptionConstants.EXECUTION_INTERRUPTED, thrown.getMessage());
        assertNotNull(thrown.getCause(), "Interrupt should be preserved as the cause");
        assertInstanceOf(InterruptedException.class, thrown.getCause());
    }

    @Test
    void timeoutStopsFurtherRetriesAfterTheCurrentAttempt() {
        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxAttempts(5)
                .timeoutInMillis(10)
                .backoffStrategy(new FixedBackoffStrategy(1))
                .retryPredicate(new AlwaysRetryPredicate())
                .build();

        CoreRetryExecutor executor = new CoreRetryExecutor(retryPolicy);
        AtomicInteger attempts = new AtomicInteger();

        CheeseRetryExecutorException exception = assertThrows(
                CheeseRetryExecutorException.class,
                () -> executor.execute(() -> {
                    attempts.incrementAndGet();
                    Thread.sleep(30);
                    throw new Exception("boom");
                })
        );

        assertEquals(1, attempts.get(), "Timeout should stop retries after the first attempt");
        assertEquals(ExceptionConstants.EXECUTION_TIMEOUT_REACHED, exception.getMessage());
        assertNotNull(exception.getCause());
    }
}
