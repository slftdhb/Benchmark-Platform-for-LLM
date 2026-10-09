package com.evalforge.benchmark.exception;

public class BenchmarkNotFoundException extends RuntimeException {
    public BenchmarkNotFoundException(Long id) {
        super("ERROR 404:   ID not found : " + id);
    }
}