package com.evalforge.evaluation.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.evalforge.benchmark.entity.BenchmarkSample;
import com.evalforge.benchmark.entity.BenchmarkVersion;
import com.evalforge.benchmark.enums.BenchmarkVersionStatus;
import com.evalforge.benchmark.mapper.BenchmarkMapper;
import com.evalforge.benchmark.mapper.BenchmarkSampleImportMapper;
import com.evalforge.benchmark.mapper.BenchmarkVersionMapper;
import com.evalforge.common.error.BusinessException;
import com.evalforge.common.error.ErrorCode;
import com.evalforge.dto.ModelEndpointResponse;
import com.evalforge.entity.ModelEndpoint;
import com.evalforge.evaluation.dto.CreateEvaluationJobRequest;
import com.evalforge.evaluation.dto.CreateEvaluationTaskRequest;
import com.evalforge.evaluation.dto.EvaluationJobResponse;
import com.evalforge.evaluation.entity.EvaluationJob;
import com.evalforge.evaluation.enums.TaskStatus;
import com.evalforge.evaluation.mapper.EvaluationJobMapper;
import com.evalforge.evaluation.mapper.EvaluationTaskMapper;
import com.evalforge.idempotency.IdempotencyMapper;
import com.evalforge.mapper.ModelEndpointMapper;

import tools.jackson.databind.ObjectMapper;


@Service
public class EvaluationJobService {
            private final BenchmarkMapper benchmarkMapper;
            private final BenchmarkVersionMapper versionMapper;
            private final IdempotencyMapper idempotencyMapper;
            private final EvaluationJobMapper evaluationJobMapper;
            private final ModelEndpointMapper modelMapper;
            private final BenchmarkSampleImportMapper sampleMapper;
            private final EvaluationTaskService taskService;
            private final ObjectMapper objectMapper;

            

            public EvaluationJobService(BenchmarkMapper benchmarkMapper, BenchmarkVersionMapper versionMapper, 
                IdempotencyMapper idempotencyMapper, EvaluationJobMapper evaluationJobMapper, ModelEndpointMapper modelMapper,
                BenchmarkSampleImportMapper sampleMapper,EvaluationTaskService taskService,ObjectMapper objectMapper
            ) {
                
                this.benchmarkMapper = benchmarkMapper;
                this.versionMapper = versionMapper;
                this.idempotencyMapper = idempotencyMapper;
                this.evaluationJobMapper = evaluationJobMapper;
                this.modelMapper = modelMapper;
                this.sampleMapper = sampleMapper;
                this.taskService = taskService;
                this.objectMapper = objectMapper;
            }
            
            //*
            // 接受http请求
            // Jwt获取CurrentUserId
            // 校验modelId
            // 校验VersionId
            // 检查Version状态，只允许PUBLISHED版本
            // 检查用户权限能否访问
            // 校验配置参数，batchSize和config
            // 检查IdempotencyKey
            // 开始数据库事务
            // 创建Job
            // 根据配置参数创建task
            // COMMIT数据库事务，否则回滚
            // 返回Job_id（202） */

            @Transactional
            public Long createEvaluationJob(Long userId,CreateEvaluationJobRequest request,String idempotencyKey){
                        //校验modelId存在
                        ModelEndpointResponse model = modelMapper.findById(request.modelId()).orElseThrow(() -> {
                                    throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,"model not found");
                        });
                        if(!model.enabled()){
                            throw new BusinessException(ErrorCode.VALIDATION_FAILED,"model is not enabled");
                        }

                        //校验VersionId,同时检查这个版本能不能被用户访问
                        BenchmarkVersion benchmarkVersion =versionMapper.selectOwnedById(userId,request.versionId());

                        if(benchmarkVersion == null) {
                            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,"version not found for user");
                        }
                        //校验版本状态
                        if(!benchmarkVersion.getStatus().equals(BenchmarkVersionStatus.PUBLISHED.name())) {
                            throw new BusinessException(ErrorCode.VALIDATION_FAILED,"only published versions can be used for evaluation");
                        }

                        if(request.configJson() == null){
                            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "invalid config");
                        }

                        EvaluationJob job = new EvaluationJob();
                        job.setJobName(request.jobName());
                        job.setOwnerId(userId);
                        job.setModelEndpointId(request.modelId());
                        job.setBenchmarkVersionId(request.versionId());
                        job.setRequestId(request.requestId()); 
                        job.setConfigJson(objectMapper.writeValueAsString(request.configJson()));

                        int affectedRows = evaluationJobMapper.createJob(job);
                        if(affectedRows != 1) {
                            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "failed to create evaluation job");
                        }

                        //根据sample列表创建task
                        List<BenchmarkSample> samples = sampleMapper.selectAllByUserIdAndVersionId(userId, request.versionId());
                        if(samples == null || samples.isEmpty()) {
                            throw new BusinessException(ErrorCode.VALIDATION_FAILED, "no valid samples found for evaluation");
                        }
                        CreateEvaluationTaskRequest taskRequest = new CreateEvaluationTaskRequest(
                                            samples,
                                            job.getId(),
                                            request.batchSize()

                        );
                        boolean createTasksFlag = taskService.createTask(taskRequest);

                        if(!createTasksFlag) {
                            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "failed to create evaluation tasks");
                        }

                        //保存幂等结果

                return job.getId(); // 临时返回null，实际应返回创建的Job_id
            }

            public EvaluationJobResponse getEvaluationJob(Long userId, Long jobId){
                    EvaluationJob job =evaluationJobMapper.getJobById(userId, jobId);
                    if(job == null){
                            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "evaluation job not found");
                    }
                    return EvaluationJobResponse.toResponse(job,objectMapper);
            }

            public List<EvaluationJobResponse> getCurrentUserEvaluationJobs(Long userId){
                    return evaluationJobMapper.getJobsByUserId(userId).stream().map(job -> EvaluationJobResponse.toResponse(job, objectMapper)).toList();
            }

            //在取消评测任务时需要同步更新task的状态为CANCELED
            @Transactional 
            public void cancelEvaluationJob(Long userId, Long jobId){
                    EvaluationJob job = evaluationJobMapper.getJobById(userId, jobId);
                    if(job == null) {
                            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "evaluation job not found");
                    }   
                    taskService.cancelTasksByJobId(jobId);
                    evaluationJobMapper.cancelJob(userId, jobId);
            }

            public void simulateEvaluationJob(Long userId, Long jobId){
                // 临时实现，实际应模拟对应的EvaluationJob
            }
}
