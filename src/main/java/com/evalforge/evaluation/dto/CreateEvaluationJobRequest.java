package com.evalforge.evaluation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import tools.jackson.databind.JsonNode;

public record CreateEvaluationJobRequest(
              String jobName,
              @NotNull
              Long modelId,
              @NotNull
              Long versionId,
              @NotNull
              String requestId,
              @NotNull 
              @Min(1)
              @Max(100)
              @Positive 
              Integer batchSize,
              JsonNode configJson
) {
    
}
