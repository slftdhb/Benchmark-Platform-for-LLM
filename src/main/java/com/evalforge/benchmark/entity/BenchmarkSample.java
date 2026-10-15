package com.evalforge.benchmark.entity;

import java.time.LocalDateTime;

import com.evalforge.benchmark.dto.BenchmarkSampleImportDTO;

import io.swagger.v3.oas.models.security.SecurityScheme.In;

public class BenchmarkSample {
    private Long id;
    private Long versionId;
    private String sampleKey;
    private String inputJson;
    private String goldJson;
    private String metadataJson;
    private Integer lineNo;
    private java.time.LocalDateTime createdAt;


    public BenchmarkSample convert(Long versionId, Integer lineNo, BenchmarkSampleImportDTO dto) {
        this.versionId = versionId;
        this.lineNo = lineNo;
        this.sampleKey = dto.sampleKey();
        this.inputJson = dto.input().toString();
        this.goldJson = dto.gold() == null ? null :dto.gold().toString();
        this.metadataJson = dto.metadata() == null ? null : dto.metadata().toString();
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getSampleKey() {
        return sampleKey;
    }

    public void setSampleKey(String sampleKey) {
        this.sampleKey = sampleKey;
    }

    public String getInputJson() {
        return inputJson;
    }
    public void setInputJson(String inputJson) {
        this.inputJson = inputJson;
    }

    public String getGoldJson() {
        return goldJson;
    }

    public void setGoldJson(String goldJson) {
        this.goldJson = goldJson;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    public Integer getLineNo() {
        return lineNo;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }  
    

}