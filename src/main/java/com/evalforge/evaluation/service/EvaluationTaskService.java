package com.evalforge.evaluation.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evalforge.common.error.BusinessException;
import com.evalforge.common.error.ErrorCode;
import com.evalforge.evaluation.dto.CreateEvaluationTaskRequest;
import com.evalforge.evaluation.dto.EvaluationTaskResponse;
import com.evalforge.evaluation.entity.EvaluationTask;
import com.evalforge.evaluation.enums.TaskStatus;
import com.evalforge.evaluation.mapper.EvaluationTaskMapper;

@Service 
public class EvaluationTaskService {

        private final EvaluationTaskMapper evaluationTaskMapper;

        public EvaluationTaskService(EvaluationTaskMapper evaluationTaskMapper) {
                this.evaluationTaskMapper = evaluationTaskMapper;
        }

            //创建task,获取创建的TaskId集合
            @Transactional 
            public List<Long> createTask(CreateEvaluationTaskRequest taskRequest) {
                    List<Long> taskIds = new ArrayList<>();
                    if(taskRequest.samples().isEmpty()) {
                            return taskIds;
                    }

                    int taskGroup = taskRequest.samples().size() / taskRequest.batchSize();


                    //batchNo从1开始算
                    for(int i = 0 ; i < taskGroup ; i++) {
                        //Service默认是单例模式，如果把task作为类私有对象，在多请求下会导致task数据被不同job覆盖
                        //把对象放进循环里，避免多请求下数据被覆盖
                        EvaluationTask task = new EvaluationTask();
                            task.setJobId(taskRequest.jobId());    
                            task.setBatchNo(i+1);
                            task.setStatus(TaskStatus.PENDING.name());
                            task.setStartSampleId(taskRequest.samples().get(i*taskRequest.batchSize()).getId());
                            task.setEndSampleId(taskRequest.samples().get((i+1)*taskRequest.batchSize() - 1).getId());
                            int affectedRows = evaluationTaskMapper.createTask(task);

                            if(affectedRows != 1 || task.getId() == null){
                                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "failed to create evaluation task");
                            }
                            taskIds.add(task.getId());
                    }
                    //还有未处理的部分，作为一个batch
                    if(taskRequest.samples().size() % taskRequest.batchSize() != 0) {
                            int i = taskGroup;
                            EvaluationTask task = new EvaluationTask();
                            task.setJobId(taskRequest.jobId());    
                            task.setBatchNo(i+1);
                            task.setStatus(TaskStatus.PENDING.name());
                            task.setStartSampleId(taskRequest.samples().get(i*taskRequest.batchSize()).getId());
                            task.setEndSampleId(taskRequest.samples().get(taskRequest.samples().size() - 1).getId());

                            int affectedRows = evaluationTaskMapper.createTask(task);
                            if(affectedRows != 1 || task.getId() == null){
                                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "failed to create evaluation task");
                            }
                            taskIds.add(task.getId());
                    }
                    return taskIds;
            }


            //在这里完成对tasks的取消
            public void cancelTasksByJobId(Long jobId) {
                    List<EvaluationTaskResponse> tasks = evaluationTaskMapper.getTasksByJobId(jobId);
                    for(EvaluationTaskResponse t : tasks) {
                        evaluationTaskMapper.cancelTasksById(t.id());
                    }
            }
    
}
