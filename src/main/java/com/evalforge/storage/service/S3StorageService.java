package com.evalforge.storage.service;

import com.evalforge.storage.config.S3Properties;
import com.evalforge.storage.entity.StoredObject;
import com.evalforge.storage.mapper.StorageMapper;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Service 
public class S3StorageService implements StorageService {

        private final S3Client s3client;

        public S3StorageService(S3Client s3client) {
                this.s3client = s3client;
        }


        @Override
        public void uploadFile(String bucketName, String key, Path file ,String contentType) {
                s3client.putObject(buildPutObjectRequest(bucketName, key, contentType),
                        RequestBody.fromFile(file));
        }

        @Override 
        public void uploadFile(String bucketName, String key, InputStream input, long contentLength, String contentType) {
                s3client.putObject(buildPutObjectRequest(bucketName, key, contentType),
                        RequestBody.fromInputStream(input, contentLength));
        }
        
        @Override 
        public void delete(String bucketName, String key) {
                s3client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build());
        }

        @Override 
        public InputStream download(String bucketName, String key) {
                return s3client.getObject(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build());
        }

        @Override 
        public boolean exists(String bucketName, String key) {
                try{
                    s3client.headObject(HeadObjectRequest.builder().bucket(bucketName).key(key).build());
                    return true;
                }catch(S3Exception e){
                    if(e.statusCode() == 404){
                        return false;
                    }
                    throw e;
                }
        }

        private PutObjectRequest buildPutObjectRequest(String bucketName, String key, String contentType) {
                PutObjectRequest.Builder request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key);

                if(contentType != null && !contentType.isBlank()){
                    request.contentType(contentType);
                }

                return request.build();
        }

}
