package com.evalforge.benchmark.service;

import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.evalforge.benchmark.dto.BenchmarkImportResponse;
import com.evalforge.benchmark.entity.BenchmarkVersion;
import com.evalforge.benchmark.enums.BenchmarkVersionStatus;
import com.evalforge.benchmark.exception.BenchmarkVersionNotFoundException;
import com.evalforge.benchmark.mapper.BenchmarkVersionMapper;
import com.evalforge.common.error.BusinessException;
import com.evalforge.common.error.ErrorCode;
import com.evalforge.storage.config.S3Properties;
import com.evalforge.storage.entity.StoredObject;
import com.evalforge.storage.service.S3StorageService;
import com.evalforge.storage.service.StorageService;
import com.evalforge.storage.service.StoredObjectService;
import com.evalforge.storage.enums.StorageStatus;
import com.evalforge.storage.enums.StorageType;

@Service 
public class BenchmarkImportOrchestator {   
            private final BenchmarkVersionMapper versionMapper;
            private final S3StorageService storageService;
            private final BenchmarkImportService benchmarkImportService;
            private final StoredObjectService storedObjectService;
            private final S3Properties props;

            private final static Logger log = LoggerFactory.getLogger(BenchmarkImportOrchestator.class);

            public BenchmarkImportOrchestator(BenchmarkVersionMapper versionMapper, S3StorageService storageService, BenchmarkImportService benchmarkImportService, StoredObjectService storedObjectService, S3Properties props) {
                this.versionMapper = versionMapper;
                this.storageService = storageService;
                this.benchmarkImportService = benchmarkImportService;
                this.storedObjectService = storedObjectService;
                this.props = props;
            }
            
            /*
            1.权限校验和文件校验
            2.生成唯一的对象key
            3.创建pending记录
            4.上传
            5.失败时做补偿状态记录
            6.finally清理
            */
            public BenchmarkImportResponse importFile(Long userId, Long versionId, MultipartFile file){
                BenchmarkVersion version = versionMapper.selectOwnedById(userId, versionId);
                if(version == null) {
                    throw new BenchmarkVersionNotFoundException(versionId);
                }
                // 2. 确认版本为 DRAFT
                if(!BenchmarkVersionStatus.DRAFT.getValue().equals(version.getStatus())) {
                    throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Benchmark version is not in DRAFT status");
                }

                // 3. 确认文件不为空
                if(file == null || file.isEmpty()) {
                    throw new BusinessException(ErrorCode.VALIDATION_FAILED, "File cannot be null or empty");
                }
                String contentType = (file.getContentType() == null || file.getContentType().isBlank()) ? "application/json" : file.getContentType();

                //生成对象的key,以及元数据
                String ObjectKey = "benchmarks/%d/%d/%s-%s".formatted(userId,versionId,UUID.randomUUID(),file.getOriginalFilename());

                StoredObject storedObject = new StoredObject();
                storedObject.setBucketName(props.benchmarkBucket());
                storedObject.setObjectKey(ObjectKey);
                storedObject.setOwnerId(userId);
                storedObject.setRelatedResourceId(versionId);
                storedObject.setStatus(StorageStatus.PENDING.name());
                storedObject.setContentType(contentType);
                storedObject.setObjectType(StorageType.BENCHMARK_SOURCE.name());
                storedObject.setOriginalFilename(file.getOriginalFilename());
                storedObject.setSizeBytes(file.getSize());

                //这里创建元纪录
                storedObjectService.createPending(storedObject);

                // 4. 上传文件
                try(InputStream in = file.getInputStream()){
                    storageService.uploadFile(props.benchmarkBucket(), ObjectKey, in, file.getSize(), contentType);
                }catch(Exception e){
                    storedObjectService.markFailed(storedObject.getId(), "S3 upload failed");
                    throw new BusinessException(ErrorCode.STORAGE_UPLOAD_FAILED, "Failed to upload source file",Map.of("versionId",versionId),e);
                }

                //文件成功导入后,这个mysql中进行状态的更新和原子事务
                //若事务失败，则删除存储对象记录
                BenchmarkImportResponse benchmarkImportResponse = null;
                try{
                    benchmarkImportResponse = benchmarkImportService.importJsonl(userId, versionId, file, storedObject.getId());
                }catch(Exception e){
                    safeDelete(storedObject);
                    throw e;
                }
                return benchmarkImportResponse;

            }


            private void safeDelete(StoredObject storedObject) {
                try{
                    storageService.delete(storedObject.getBucketName(), storedObject.getObjectKey());
                }catch(Exception e){
                    log.warn("fail to delete S3 source file key={} ", storedObject.getObjectKey(), e);
                }
                try{
                    storedObjectService.delete(storedObject.getId());
                }catch(Exception e){
                    log.warn("fail to delete stored object record id={} ", storedObject.getId(), e);
                }
            }
}   
