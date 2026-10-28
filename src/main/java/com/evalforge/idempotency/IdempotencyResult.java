package com.evalforge.idempotency;

public record IdempotencyResult(
    Long resourceId,
    boolean replayed
) {
    
}
