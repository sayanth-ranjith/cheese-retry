package com.sayanth_ranjith.cheese_retry_core.core.exception;

public class ExceptionConstants {
    public static final String MAX_ATTEMPS_LESS_THAN_EQUAL_TO_ZERO = "Maximum attempts cannot be less than or equal to zero.";
    public static final String BACK_OFF_STRATEGY_NULL = "Backoff strategy cannot be null";
    public static final String RETRY_PREDICATE_NULL = "Retry predicate cannot be null";

    public static final String EXECPTION_ATTEMPTS_REACHED = "Maximum attempts reached, unable to retry after max.";
    public static final String EXCEPTION_RETRY_PREDICATE = "Retry predicate commands not to retry.";
}
