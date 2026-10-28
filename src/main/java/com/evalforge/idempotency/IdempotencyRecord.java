package com.evalforge.idempotency;

import java.time.LocalDateTime;

public record IdempotencyRecord(
    Long id,
    String operation,
    String idempotencyKey,
    String requestHash,
    Long resourceId,
    String status,
    LocalDateTime createdAt
){
    public IdempotencyRecord(Long id){
        this(id,null,null,null,null,null,null);
    }
    public IdempotencyRecord(String operation,String idempotencyKey,String requestHash){
        this(null,operation,idempotencyKey,requestHash,null,null,null);
    }
}