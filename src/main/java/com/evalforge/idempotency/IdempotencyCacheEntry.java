package com.evalforge.idempotency;

public record IdempotencyCacheEntry(
            String requestHash,
            Long resourceId
){

}