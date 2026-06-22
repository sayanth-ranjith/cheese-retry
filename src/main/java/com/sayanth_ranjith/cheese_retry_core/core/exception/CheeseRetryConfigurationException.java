package com.sayanth_ranjith.cheese_retry_core.core.exception;

public class CheeseRetryConfigurationException extends IllegalArgumentException {

    public CheeseRetryConfigurationException(String message) {
        super(message);
    }

    public CheeseRetryConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
