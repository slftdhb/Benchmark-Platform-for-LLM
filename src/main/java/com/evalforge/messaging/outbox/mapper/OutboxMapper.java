package com.evalforge.messaging.outbox.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.evalforge.messaging.outbox.entity.OutboxEvent;

@Mapper 
public interface OutboxMapper {

        int insertPending(@Param("event") OutboxEvent event);

        List<OutboxEvent> findDue(@Param("limit") int limit);

        int claim(@Param("id") Long id ,
                  @Param("lockedBy") String lockedBy,
                  @Param("lockedUntil") LocalDateTime lockedUntil);
        
        int markPublished(@Param("id") Long id,
                          @Param("publishedAt") LocalDateTime publishedAt);

        int markFailure(@Param("id") Long id,
                        @Param("lastError") String lastError,
                        @Param("availableAt") LocalDateTime availableAt,
                        @Param("maxAttempts") int maxAttempts);

        int countByStatus(@Param("status") String status);
}
