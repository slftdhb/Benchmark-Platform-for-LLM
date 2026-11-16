package com.evalforge.messaging.dto;

import java.time.Instant;

public record TaskDispatchMessage(
        int schemaVersion,
        String eventId,
        Long jobId,
        Long taskId,
        Instant timestamp
) {
    
}
