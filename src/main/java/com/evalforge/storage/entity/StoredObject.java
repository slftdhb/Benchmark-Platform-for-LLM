package com.evalforge.storage.entity;


import java.time.LocalDateTime;


public class StoredObject {
    
    private Long id;
    private Long ownerId;
    private String bucketName;
    private String objectKey;
    private String objectType;
    private Long relatedResourceId;
    private String originalFilename;
    private String contentType;
    private Long sizeBytes;
    private String sha256;
    private String status;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    public String getBucketName() {
        return bucketName;
    }
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
    public String getObjectKey() {
        return objectKey;
    }
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }
    public String getObjectType() {
        return objectType;
    }
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
    public Long getRelatedResourceId() {
        return relatedResourceId;
    }
    public void setRelatedResourceId(Long relatedResourceId) {
        this.relatedResourceId = relatedResourceId;
    }
    public String getOriginalFilename() {
        return originalFilename;
    }
    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public Long getSizeBytes() {
        return sizeBytes;
    }
    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }
    public String getSha256() {
        return sha256;
    }
    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


}
