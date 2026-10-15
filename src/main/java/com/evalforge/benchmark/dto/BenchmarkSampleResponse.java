package com.evalforge.benchmark.dto;

import java.time.LocalDateTime;

import tools.jackson.databind.JsonNode;


//用于预览样本
public record BenchmarkSampleResponse(
            Long id,
            Long versionId,
            String sampleKey,
            JsonNode inputJson,
            JsonNode goldJson,
            JsonNode metadataJson,
            Integer lineNo,
            LocalDateTime createdAt
        
){

}