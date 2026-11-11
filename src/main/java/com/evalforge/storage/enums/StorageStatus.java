package com.evalforge.storage.enums;

public enum StorageStatus {
    PENDING("PENDING"),
    READY("READY"),
    FAILED("FAILED"),
    CLEANUP_PENDING("CLEANUP_PENDING");

    private final String value;

    StorageStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
