package com.evalforge.idempotency;

import java.time.LocalDateTime;

public record IdempotencyRecord(
    Long id,
    Long userId,
    String operation,
    String idempotencyKey,
    String requestHash,
    Long resourceId,
    String status,
    LocalDateTime createdAt
){
    public IdempotencyRecord(Long id){
        this(id,null,null,null,null,null,null,null);
    }
    public IdempotencyRecord(Long userId,String operation,String idempotencyKey,String requestHash){
        this(null,userId,operation,idempotencyKey,requestHash,null,null,null);
    }
}