package com.evalforge.benchmark.dto;

import java.time.LocalDateTime;

public record BenchmarkResponse(

        Long id,
        String name,
        String description,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String category

){

}