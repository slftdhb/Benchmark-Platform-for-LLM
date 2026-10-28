package com.evalforge.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import com.evalforge.entity.ModelEndpoint;
import com.evalforge.ratelimit.ModelRateLimitException;
import com.evalforge.ratelimit.RateLimitResult;
import com.evalforge.ratelimit.RedisModelRateLimiter;
import com.evalforge.service.ModelEndpointService;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import com.evalforge.dto.UpdateModelEndpointRequest;
import com.evalforge.dto.CreateModelEndpointRequest;
import com.evalforge.dto.ModelEndpointResponse;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.apache.catalina.filters.RateLimitFilter;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/models")

public class ModelEndpointController {
    //依赖注入阶段的构造器注入
    private final ModelEndpointService service;

    private final RedisModelRateLimiter limiter;

    public ModelEndpointController(ModelEndpointService service,RedisModelRateLimiter limiter) {
        this.service = service;
        this.limiter=limiter;
    }

    //这是一个固定窗口限流demo接口
    @PostMapping("/{id}/inference-demo")
    public boolean getModelInferenceByModelId(@PathVariable("id") long modelId){
        RateLimitResult result = limiter.acquire(modelId);

        if(!result.allowed()){
            throw new ModelRateLimitException(modelId, result.retryAfterSeconds());
        }
        return result.allowed();
    }

    @PostMapping
    public CreateModelEndpointRequest create(@Valid @RequestBody CreateModelEndpointRequest request){
        return this.service.create(request);
    }

    @GetMapping
    public List<ModelEndpointResponse> findAll(){
        return this.service.findAll();
    }

    @GetMapping("/{id}")
    public ModelEndpointResponse findById(@PathVariable("id") long id){
        return this.service.findById(id);
    }
    
    @PutMapping("/{id}")
    public ModelEndpointResponse update(@PathVariable("id") long id, @RequestBody UpdateModelEndpointRequest request){
        return service.update(id, request);
    }


    //删除映射的区别其他三个，删除操作返回的是void，并且使用@ResponseStatus(HttpStatus.NO_CONTENT)表示删除成功
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") long id){
        service.delete(id);
    }
}