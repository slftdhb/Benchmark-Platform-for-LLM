package com.evalforge.benchmark.exception;

public class BenchmarkVersionNotFoundException extends RuntimeException {
    public BenchmarkVersionNotFoundException(Long id) {
        super("Benchmark version not found with id: " + id);
    }
}