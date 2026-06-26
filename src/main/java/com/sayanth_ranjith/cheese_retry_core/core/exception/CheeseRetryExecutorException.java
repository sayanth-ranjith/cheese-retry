package com.sayanth_ranjith.cheese_retry_core.core.exception;

public class CheeseRetryExecutorException extends RuntimeException {
    public CheeseRetryExecutorException(String message) {
        super(message);
    }
    public CheeseRetryExecutorException(String message, Throwable cause) {
        super(message, cause);
    }
}
