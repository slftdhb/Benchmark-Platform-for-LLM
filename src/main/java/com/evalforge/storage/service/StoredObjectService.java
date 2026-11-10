package com.evalforge.storage.service;

import org.springframework.stereotype.Service;

import com.evalforge.common.error.BusinessException;
import com.evalforge.common.error.ErrorCode;
import com.evalforge.storage.entity.StoredObject;
import com.evalforge.storage.enums.StorageStatus;
import com.evalforge.storage.enums.StorageType;
import com.evalforge.storage.mapper.StorageMapper;

@Service 
public class StoredObjectService {
        private final StorageMapper storageMapper;

        //这个类用来实现存储对象相关的mysql业务逻辑
        public StoredObjectService(StorageMapper storageMapper) {
                this.storageMapper = storageMapper;
        }

        public StoredObject createPending(StoredObject storedObject) {
                int affectedRows = storageMapper.insert(storedObject);
                if (affectedRows != 1){
                    throw new BusinessException(ErrorCode.STORAGE_UPLOAD_FAILED, "failed to create stored object record");
                }
                return storedObject;
        }
        
        public void markReady(Long id, String sha256) {
        int rows = storageMapper.updateStatusAndSha256(
                id, StorageStatus.READY.name(), sha256);
        if (rows != 1) {
            throw new BusinessException(ErrorCode.STORAGE_UPLOAD_FAILED,
                    "Failed to mark stored object READY");
        }
        }

        public void markFailed(Long id, String message) {
            storageMapper.updateStatusAndError(id, StorageStatus.FAILED.name(), message);
        }

        public StoredObject findReadySource(Long ownerId, Long versionId) {
            return storageMapper.selectReadyByResource(
                    ownerId, StorageType.BENCHMARK_SOURCE.name(), versionId);
        }

        public void delete(Long id) {
            storageMapper.delete(id);
        }

}
