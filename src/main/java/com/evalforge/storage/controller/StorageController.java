package com.evalforge.storage.controller;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.evalforge.benchmark.entity.BenchmarkVersion;
import com.evalforge.benchmark.mapper.BenchmarkVersionMapper;
import com.evalforge.common.error.BusinessException;
import com.evalforge.common.error.ErrorCode;
import com.evalforge.security.CurrentUser;
import com.evalforge.storage.entity.StoredObject;
import com.evalforge.storage.service.S3StorageService;
import com.evalforge.storage.service.StoredObjectService;

import software.amazon.awssdk.services.s3.model.S3Exception;

@RestController 
@RequestMapping("/api/v1")
public class StorageController {   
        private final S3StorageService storageService;
        private final BenchmarkVersionMapper versionMapper;
        private final StoredObjectService storedObjectService;

        public StorageController(S3StorageService storageService, BenchmarkVersionMapper versionMapper,StoredObjectService storedObjectService) {
            this.storageService = storageService;
            this.versionMapper = versionMapper;
            this.storedObjectService=storedObjectService;
        }
    
        @GetMapping ("/benchmark-versions/{versionId}/source/download")
        public ResponseEntity<StreamingResponseBody> downloadBenchmarkSource(@AuthenticationPrincipal CurrentUser currentUser, @PathVariable Long versionId) {
                Long userId = currentUser.id();

                BenchmarkVersion version = versionMapper.selectOwnedById(userId, versionId);
                if (version == null) {
                    throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Version not found");
                }

                StoredObject source = storedObjectService.findReadySource(userId, versionId);
                if (source == null) {
                    throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Source file not found");
                }

                InputStream input;
                try{
                    input = storageService.download(source.getBucketName(), source.getObjectKey());
                }catch(S3Exception e ){
                    if(e.statusCode() == 404){
                        throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Source file not found");
                    }
                    throw e;    
                }

                 StreamingResponseBody body = outputStream -> {
                    try (input) {
                        input.transferTo(outputStream);
                    }
                };

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                ContentDisposition.attachment()
                                        .filename(source.getOriginalFilename(), StandardCharsets.UTF_8)
                                        .build().toString())
                        .contentType(parseMediaType(source.getContentType()))
                        .contentLength(source.getSizeBytes())
                        .body(body);
        }

            private MediaType parseMediaType(String value) {
            try {
                return MediaType.parseMediaType(value);
            } catch (Exception e) {
                return MediaType.APPLICATION_OCTET_STREAM;
            }
}

        @GetMapping ("/evaluation-jobs/{jobId}/outputs/download")
        public void downloadEvaluationOutputs(@AuthenticationPrincipal CurrentUser currentUser, @PathVariable Long jobId) {
            
        }
}
