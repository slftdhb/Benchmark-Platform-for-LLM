package com.evalforge.storage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.evalforge.storage.entity.StoredObject;

@Mapper 
public interface StorageMapper {
        int insert(@Param("storedObject") StoredObject storedObject);

        int updateStatus(@Param("id") Long id, 
                         @Param("status") String status);

        int updateSha256(@Param("id") Long id, 
                         @Param("sha256") String sha256);

        int updateStatusAndSha256(@Param("id") Long id,
                              @Param("status") String status,
                              @Param("sha256") String sha256);

        int updateStatusAndError(@Param("id") Long id,
                             @Param("status") String status,
                             @Param("errorMessage") String errorMessage);


        StoredObject selectById(@Param("id") Long id);

        StoredObject selectReadyByResource(@Param("ownerId") Long ownerId,
                                       @Param("objectType") String objectType,
                                       @Param("relatedResourceId") Long relatedResourceId);

        void delete(@Param("id") Long id);

        int updateStatusForCleanUp(@Param("id") Long id);
}
