package com.evalforge.benchmark.dto;

import tools.jackson.databind.JsonNode;

public record BenchmarkSampleImportDTO(

    String sampleKey,

    JsonNode input,

    JsonNode gold,

    JsonNode metadata
) {


}