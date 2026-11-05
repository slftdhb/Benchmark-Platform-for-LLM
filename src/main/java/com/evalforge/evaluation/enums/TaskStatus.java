package com.evalforge.evaluation.enums;

public enum TaskStatus {
            PENDING("PENDING"),
            RUNNING("RUNNING"),
            SUCCEEDED("SUCCEEDED"),
            FAILED("FAILED"),
            CANCELLING("CANCELLING"),
            CANCELLED("CANCELLED");

            private final String value;

            TaskStatus(String value){
                this.value = value;
            }

            public String getValue(){
                return value;
            }
}
