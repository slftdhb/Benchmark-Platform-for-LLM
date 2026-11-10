package com.evalforge.storage.enums;

public enum StorageType {
    BENCHMARK_SOURCE("BENCHMARK_SOURCE"),
    EVALUATION_OUTPUT("EVALUATION_OUTPUT");

    private final String value;

    StorageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
}
