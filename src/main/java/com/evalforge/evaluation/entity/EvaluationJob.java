package com.evalforge.evaluation.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import tools.jackson.databind.JsonNode;

public class EvaluationJob {

             Long id;
             String jobName;
             Long ownerId;
             Long modelEndpointId;
             Long benchmarkVersionId;
             String requestId;
             String status;
             Integer total;
             Integer succeed;
             Integer failed;
             String configJson;
             String configHash;
             LocalDateTime createdAt;
             LocalDateTime finishedAt;
             LocalDateTime startedAt;
             
             public Long getId() {
                 return id;
             }
             public void setId(Long id) {
                 this.id = id;
             }
             public String getJobName() {
                 return jobName;
             }
             public void setJobName(String jobName) {
                 this.jobName = jobName;
             }
             public Long getOwnerId() {
                 return ownerId;
             }
             public void setOwnerId(Long ownerId) {
                 this.ownerId = ownerId;
             }
             public Long getModelEndpointId() {
                 return modelEndpointId;
             }
             public void setModelEndpointId(Long modelEndpointId) {
                 this.modelEndpointId = modelEndpointId;
             }
             public Long getBenchmarkVersionId() {
                 return benchmarkVersionId;
             }
             public void setBenchmarkVersionId(Long benchmarkVersionId) {
                 this.benchmarkVersionId = benchmarkVersionId;
             }
             public String getRequestId() {
                return requestId;
            }
             public void setRequestId(String requestId) {
                 this.requestId = requestId;
             }
             public String getStatus() {
                 return status;
             }
             public void setStatus(String status) {
                 this.status = status;
             }
             public Integer getTotal() {
                 return total;
             }
             public void setTotal(Integer total) {
                 this.total = total;
             }
             public Integer getSucceed() {
                 return succeed;
             }
             public void setSucceed(Integer succeed) {
                 this.succeed = succeed;
             }
             public Integer getFailed() {
                 return failed;
             }
             public void setFailed(Integer failed) {
                 this.failed = failed;
             }
             public String getConfigJson() {
                return configJson;
            }
             public void setConfigJson(String configJson) {
                 this.configJson = configJson;
             }
             public String getConfigHash() {
                 return configHash;
             }
             public void setConfigHash(String configHash) {
                 this.configHash = configHash;
             }
             public LocalDateTime getCreatedAt() {
                 return createdAt;
             }
             public void setCreatedAt(LocalDateTime createdAt) {
                 this.createdAt = createdAt;
             }
             public LocalDateTime getFinishedAt() {
                 return finishedAt;
             }
             public void setFinishedAt(LocalDateTime finishedAt) {
                 this.finishedAt = finishedAt;
             }
             public LocalDateTime getStartedAt() {
                 return startedAt;
             }
             public void setStartedAt(LocalDateTime startedAt) {
                 this.startedAt = startedAt;
             }


}
