package com.evalforge.evaluation.entity;

import java.time.LocalTime;

public class EvaluationTask {  
             Long id;
             Long jobId;
             Integer batchNo;
             String status;
             Integer attempt;
             Long workerId;
             String errorCode;
             Long startSampleId;
             Long endSampleId;
             LocalTime createdAt;
             LocalTime finishedAt;
             LocalTime startedAt;
             
             public Long getId() {
                 return id;
             }
             public void setId(Long id) {
                 this.id = id;
             }
             public Long getJobId() {
                 return jobId;
             }
             public void setJobId(Long jobId) {
                 this.jobId = jobId;
             }
             public Integer getBatchNo() {
                 return batchNo;
             }
             public void setBatchNo(Integer batchNo) {
                 this.batchNo = batchNo;
             }
             public String getStatus() {
                 return status;
             }
             public void setStatus(String status) {
                 this.status = status;
             }
             public Integer getAttempt() {
                 return attempt;
             }
             public void setAttempt(Integer attempt) {
                 this.attempt = attempt;
             }
             public Long getWorkerId() {
                 return workerId;
             }
             public void setWorkerId(Long workerId) {
                 this.workerId = workerId;
             }
             public String getErrorCode() {
                 return errorCode;
             }
             public void setErrorCode(String errorCode) {
                 this.errorCode = errorCode;
             }
             public Long getStartSampleId() {
                 return startSampleId;
             }
             public void setStartSampleId(Long startSampleId) {
                 this.startSampleId = startSampleId;
             }
             public Long getEndSampleId() {
                 return endSampleId;
             }
             public void setEndSampleId(Long endSampleId) {
                 this.endSampleId = endSampleId;
             }
             public LocalTime getCreatedAt() {
                 return createdAt;
             }
             public void setCreatedAt(LocalTime createdAt) {
                 this.createdAt = createdAt;
             }
             public LocalTime getFinishedAt() {
                 return finishedAt;
             }
             public void setFinishedAt(LocalTime finishedAt) {
                 this.finishedAt = finishedAt;
             }
             public LocalTime getStartedAt() {
                 return startedAt;
             }
             public void setStartedAt(LocalTime startedAt) {
                 this.startedAt = startedAt;
             }
    
}
