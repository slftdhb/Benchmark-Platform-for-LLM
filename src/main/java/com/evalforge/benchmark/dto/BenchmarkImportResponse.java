package com.evalforge.benchmark.dto;

public record BenchmarkImportResponse(
            Long versionId,
            String filename,
            int sampleCount,
            String contentHash,
            String status     
)
{
}