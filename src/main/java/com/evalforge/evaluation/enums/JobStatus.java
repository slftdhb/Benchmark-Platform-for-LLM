package com.evalforge.evaluation.enums;

public enum JobStatus {
            PENDING("PENDING"),
            RUNNING("RUNNING"),
            SUCCEEDED("SUCCEEDED"),
            FAILED("FAILED"),
            CANCELLING("CANCELLING"),
            CANCELLED("CANCELLED");

            private final String value;

            JobStatus(String value){
                this.value = value;
            }

            public String getValue(){
                return value;
            }
}
