package com.evalforge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.evalforge.dto.UpdateModelEndpointRequest;
import com.evalforge.dto.CreateModelEndpointRequest;
import com.evalforge.dto.ModelEndpointResponse;

import java.util.Optional;

@Mapper
public interface ModelEndpointMapper {
    int insert(@Param("request") CreateModelEndpointRequest request);

    Optional<ModelEndpointResponse> findById(@Param("id") long id);

    int update(@Param("id") long id, @Param("request") UpdateModelEndpointRequest request);

    List<ModelEndpointResponse> findAll();

    int delete(@Param("id") long id);
}
