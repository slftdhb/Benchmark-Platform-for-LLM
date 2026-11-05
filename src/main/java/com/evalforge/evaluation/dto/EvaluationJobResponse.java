package com.evalforge.evaluation.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.evalforge.evaluation.entity.EvaluationJob;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public record EvaluationJobResponse(
             Long id,
             String jobName,
             Long ownerId,
             Long modelEndpointId,
             Long benchmarkVersionId,
             String status,
             Integer total,
             Integer succeed,
             Integer failed,
             JsonNode configJson,
             String configHash,
             LocalDateTime createdAt,
             LocalDateTime finishedAt,
             LocalDateTime startedAt
) {

    public static EvaluationJobResponse  toResponse(EvaluationJob job,ObjectMapper objectMapper){ 
        JsonNode configJsonNode = job.getConfigJson() ==null ? null: objectMapper.readTree(job.getConfigJson());
        return new EvaluationJobResponse(
            job.getId(),
            job.getJobName(),
            job.getOwnerId(),
            job.getModelEndpointId(),
            job.getBenchmarkVersionId(),
            job.getStatus(),
            job.getTotal(),
            job.getSucceed(),
            job.getFailed(),
            configJsonNode,
            job.getConfigHash(),
            job.getCreatedAt(),
            job.getFinishedAt(),
            job.getStartedAt()
        );
    }
}
