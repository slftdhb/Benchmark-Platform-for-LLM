package com.evalforge.benchmark.dto;

import jakarta.validation.constraints.Size;

public record UpdateBenchmarkVersionRequest(
        @Size( max =1024)
        String notes
) {}