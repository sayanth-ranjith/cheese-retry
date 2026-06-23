package com.sayanth_ranjith.cheese_retry_core.core.predicate.type;

import lombok.Getter;

@Getter
public enum RetryPredicateType {
    ALWAYS_RETRY,
    NEVER_RETRY,
    TYPED_BASED_RETRY
    ;

}
