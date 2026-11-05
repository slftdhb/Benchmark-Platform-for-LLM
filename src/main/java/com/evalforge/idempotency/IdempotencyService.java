package com.evalforge.idempotency;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evalforge.benchmark.dto.CreateBenchmarkRequest;
import com.evalforge.benchmark.service.BenchmarkService;
import com.evalforge.common.error.BusinessException;
import com.evalforge.common.error.ErrorCode;
import com.evalforge.common.hasher.BenchmarkCreateRequestHasher;
import com.evalforge.common.hasher.EvaluationJobCreateRequestHasher;
import com.evalforge.evaluation.dto.CreateEvaluationJobRequest;
import com.evalforge.evaluation.service.EvaluationJobService;

import tools.jackson.databind.ObjectMapper;

@Service 
public class IdempotencyService {
            private final IdempotencyMapper idempotencyMapper;

            private final ObjectMapper objectMapper;
            private final BenchmarkService benchmarkService;
            private final BenchmarkCreateRequestHasher hasher;
            private final EvaluationJobCreateRequestHasher evaluationJobHasher;
            private final EvaluationJobService jobService;

            public IdempotencyService(IdempotencyMapper idempotencyMapper,ObjectMapper objectMapper,BenchmarkService benchmarkService,
                BenchmarkCreateRequestHasher hasher,EvaluationJobCreateRequestHasher evaluationJobHasher,
            EvaluationJobService jobService){
                this.idempotencyMapper=idempotencyMapper;
                this.objectMapper = objectMapper;
                this.benchmarkService=benchmarkService;
                this.hasher=hasher;
                this.evaluationJobHasher=evaluationJobHasher;
                this.jobService=jobService;
            }

            //这里的事务只涉及mysql，redis作为外层事务，未命中缓存时再进入mysql事务
            @Transactional 
            public Long createBenchmark(Long ownerId,CreateBenchmarkRequest request,String idempotencyKey){
                //校验Key，计算hash
                if(idempotencyKey == null || idempotencyKey.isBlank() || idempotencyKey.length() > 128 ){
                    throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Idempotency-Key must contain 1 to 128 characters");
                }

                String request_content_hash = hasher.calculateRequestHash(request);
                
                IdempotencyRecord record = new IdempotencyRecord(ownerId, "CREATE_BENCHMARK", idempotencyKey, request_content_hash);

                try{
                    //插入PROCESSING记录
                    int attempt = idempotencyMapper.insertProcessing(record);
                    //插入失败，idempotencyKey已经存在
                    if(attempt !=1){
                            throw new IllegalStateException("failed to insert idempotencyKey");
                    }
                }catch(DuplicateKeyException ex){
                            //发现已经有idempotencyKey,进行已有记录比对并返回旧id
                            IdempotencyRecord existedRecord = idempotencyMapper.selectForUpdate(ownerId, "CREATE_BENCHMARK",idempotencyKey);
                            //如果记录查不到
                            if (existedRecord == null) {
                                                        throw new IllegalStateException(
                                                                "Idempotency record disappeared");
                                                    }
                            String existedHash=existedRecord.requestHash();
                            if(existedHash.equals(request_content_hash)){
                                    if("SUCCEED".equals(existedRecord.status())){
                                        return existedRecord.resourceId();
                                    }
                            }
                            throw new BusinessException(ErrorCode.IDEMPOTENCY_KEY_CONFLICT, "Idempotency-Key was already used with different parameters");

                }
                //插入成功
                Long resource_id = benchmarkService.create(ownerId,request);
                int affectedRows = idempotencyMapper.markSucceeded(ownerId,resource_id, "CREATE_BENCHMARK", idempotencyKey);
                //这里需要判断插入成功与否，否则没插入成功，事务提交，会显示benchmark已经创建，但是幂等记录仍然在PROCESSING
                if(affectedRows != 1){
                        throw new IllegalStateException("Failed to complete idempotency record");
                }

                return resource_id;
            }

            //这里的resourceId来标识请求任务的requestId
            @Transactional
            public Long createEvaluationJob(Long ownerId,CreateEvaluationJobRequest request,String idempotencyKey){
                //校验Key，计算hash
                if(idempotencyKey == null || idempotencyKey.isBlank() || idempotencyKey.length() > 128 ){
                    throw new BusinessException(ErrorCode.VALIDATION_FAILED, "Idempotency-Key must contain 1 to 128 characters");
                }

                //用user_id和requestId来表示请求，不再去计算具体内容的hash
                String request_hash = evaluationJobHasher.calculateRequestHash(request);
                
                //查询已有记录
                IdempotencyRecord record = new IdempotencyRecord(ownerId,"CREATE_EVALUATION_JOB",idempotencyKey,request_hash);
                try{
                    int affectedRows = idempotencyMapper.insertProcessing(record);
                    if(affectedRows != 1){
                            throw new IllegalStateException("Failed to insert idempotency record");
                    }
                }catch(DuplicateKeyException ex){
                    IdempotencyRecord existedRecord = idempotencyMapper.selectForUpdate(ownerId, "CREATE_EVALUATION_JOB", idempotencyKey);
                    if(existedRecord == null){
                            throw new IllegalStateException("IdempotencyKey record disppeared");
                    }
                        if(request_hash.equals(existedRecord.requestHash())){
                                if(existedRecord.status().equals("SUCCEED")){
                                    return existedRecord.resourceId();
                                }
                        }
                        throw new BusinessException(ErrorCode.IDEMPOTENCY_KEY_CONFLICT, "Idempotency-Key was already used with different parameters");
                }
                Long resourceId = jobService.createEvaluationJob(ownerId, request, idempotencyKey);
                int affectedRows = idempotencyMapper.markSucceeded(ownerId,resourceId, "CREATE_EVALUATION_JOB", idempotencyKey);
                if(affectedRows != 1){
                        throw new IllegalStateException("Failed to complete idempotency record");
                }

                return resourceId;
            }
}
