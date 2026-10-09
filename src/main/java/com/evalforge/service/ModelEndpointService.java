package com.evalforge.service;


import org.springframework.stereotype.Service;
import com.evalforge.mapper.ModelEndpointMapper;
import com.evalforge.entity.ModelEndpoint;
import java.util.List;

import com.evalforge.dto.ModelEndpointResponse;
import com.evalforge.dto.UpdateModelEndpointRequest;
import com.evalforge.dto.CreateModelEndpointRequest;
import com.evalforge.exception.ModelEndpointNotFoundException;
@Service
public class ModelEndpointService {
    //依赖注入
    private final ModelEndpointMapper mapper;

    public ModelEndpointService(ModelEndpointMapper mapper) {
        this.mapper = mapper;
    }


    //创建一个新的模型端点
    public CreateModelEndpointRequest create(CreateModelEndpointRequest request){
        int affectedRows = mapper.insert(request);

        if (affectedRows !=1){
            throw new IllegalStateException("Failed to create model endpoint");
        }
        return request;
    }

    //根据ID查找模型端点
    public ModelEndpointResponse findById(long id){
        return mapper.findById(id).orElseThrow(()->{
                return new ModelEndpointNotFoundException(id);
        });
    }

    //查找所有的模型端点
    public List<ModelEndpointResponse> findAll(){
        return mapper.findAll();
    }

    public ModelEndpointResponse update(long id, UpdateModelEndpointRequest request){
        
        int affectedRows = mapper.update(id, request);
        //如果是0行说明没有找到对应的模型端点
        if (affectedRows == 0){
            throw new ModelEndpointNotFoundException(id);
        }
        //如果不为1说明更新操作不符合预期
        if (affectedRows != 1){
            throw new IllegalStateException("Unexpected affected rows :"+affectedRows);
        }
        //等于1后返回相应id端点
        return mapper.findById(id).orElseThrow(()->{
            return new ModelEndpointNotFoundException(id);
        }
        );
    }

    public void delete (long id){

        int affectedRows = mapper.delete(id);
        //如果是0行说明没有找到对应的模型端点
        if (affectedRows == 0){
            throw new ModelEndpointNotFoundException(id);
        }
        //如果不为1说明更新操作不符合预期
        if (affectedRows != 1){
            throw new IllegalStateException("Unexpected affected rows :"+affectedRows);
        }


    }
}