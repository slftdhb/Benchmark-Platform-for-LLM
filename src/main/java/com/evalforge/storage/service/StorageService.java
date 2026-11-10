package com.evalforge.storage.service;

import java.io.InputStream;
import java.nio.file.Path;

import com.evalforge.storage.entity.StoredObject;


public interface StorageService {
        
    void uploadFile(String bucketName, String key, Path file,String contentType);

    void uploadFile(String bucketName, String key, InputStream input,long contentLength, String contentType);

    InputStream download(String bucketName, String key);

    boolean exists(String bucketName, String key);

    void delete(String bucketName, String key);

    
}
