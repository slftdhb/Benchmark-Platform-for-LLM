package com.evalforge.evaluation.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record EvaluationTaskResponse(
                Long id,
                Long jobId,
                Integer batchNo,
                String status,
                Integer attempt,
                Long workerId,
                String errorCode,
                Long startSampleId,
                Long endSampleId,
                LocalDateTime createdAt,
                LocalDateTime finishedAt,
                LocalDateTime startedAt
    ) {
    
}
