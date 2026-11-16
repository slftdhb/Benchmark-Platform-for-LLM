package com.evalforge.messaging.outbox.enums;

public enum OutboxStatus {
    PENDING("PENDING"),
    PROCESSING("PROCESSING"),
    PUBLISHED("PUBLISHED"),
    DEAD("DEAD");

    private final String value;

    OutboxStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
