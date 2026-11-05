package com.evalforge.evaluation.dto;

import java.util.List;

import com.evalforge.benchmark.entity.BenchmarkSample;

public record CreateEvaluationTaskRequest(
                List<BenchmarkSample> samples,
                Long jobId,
                Integer batchSize

) {
    
}
