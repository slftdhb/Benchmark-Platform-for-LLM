package com.evalforge.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evalforge.mapper.ModelEndpointMapper;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.JsonNodeException;

import com.evalforge.entity.ModelEndpoint;
import com.evalforge.event.ModelChangedEvent;

import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import com.evalforge.dto.ModelEndpointResponse;
import com.evalforge.dto.UpdateModelEndpointRequest;
import com.evalforge.cache.ModelCacheService;
import com.evalforge.dto.CreateModelEndpointRequest;
import com.evalforge.exception.ModelEndpointNotFoundException;
@Service
public class ModelEndpointService {
    //依赖注入
    private final ModelEndpointMapper mapper;

    private final ModelCacheService cacheService;

    private final ApplicationEventPublisher eventPublisher;

    private final ObjectMapper objectMapper;

    public ModelEndpointService(ModelEndpointMapper mapper, ModelCacheService cacheService,ApplicationEventPublisher eventPublisher,ObjectMapper objectMapper){
        this.mapper = mapper;
        this.cacheService=cacheService;
        this.eventPublisher=eventPublisher;
        this.objectMapper=objectMapper;
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
    public ModelEndpointResponse findById(long id) throws JsonNodeException{
        //查询缓存
        String  cached = cacheService.get(id);

        //缓存命中,返回DTO
        if(cached != null){
            ModelEndpointResponse result = objectMapper.readValue(cached,ModelEndpointResponse.class);
            return result;
        }

        //查询MYSQL
        ModelEndpointResponse result = mapper.findById(id).orElseThrow(()->{
            throw new ModelEndpointNotFoundException(id);});

        //缓存回填,默认10Minutes

        String json =objectMapper.writeValueAsString(result);
        cacheService.put(id,json);

        return result;
    }

    //查找所有的模型端点
    public List<ModelEndpointResponse> findAll(){
        return mapper.findAll();
    }

    @Transactional 
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

        eventPublisher.publishEvent(
            new ModelChangedEvent(id)
        );

        //等于1后返回相应id端点
        return mapper.findById(id).orElseThrow(()->{
            return new ModelEndpointNotFoundException(id);
        }
        );
    }

    @Transactional 
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
        eventPublisher.publishEvent(new ModelChangedEvent(id));

    }
}