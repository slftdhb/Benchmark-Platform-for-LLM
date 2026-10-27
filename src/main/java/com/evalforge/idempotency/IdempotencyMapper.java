package com.evalforge.idempotency;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IdempotencyMapper {

            int insertProcessing(@Param("record") IdempotencyRecord record);

            IdempotencyRecord selectForUpdate(@Param("operation") String operation, @Param("idempotencyKey") String idempotencyKey);

            int markSucceeded(@Param("resourceId") Long resourceId,@Param("operation") String operation,@Param("idempotencyKey") String idempotencyKey);

            IdempotencyRecord selectByIdempotencyKey(@Param("idempotencyKey") String idempotencyKey);
}
