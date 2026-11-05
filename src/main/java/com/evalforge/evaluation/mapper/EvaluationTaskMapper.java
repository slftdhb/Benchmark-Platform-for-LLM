package com.evalforge.evaluation.mapper;

import com.evalforge.evaluation.dto.CreateEvaluationTaskRequest;
import com.evalforge.evaluation.dto.EvaluationTaskResponse;
import com.evalforge.evaluation.entity.EvaluationTask;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; 


@Mapper 
public interface EvaluationTaskMapper {
                //create a new evaluation task
                int createTask(@Param("task") EvaluationTask task);

                List<EvaluationTaskResponse> getTasksByJobId(@Param("jobId") Long jobId);

                EvaluationTaskResponse getTaskById(@Param("taskId") Long taskId);

                int updateTaskStatusById(@Param("taskId") Long taskId, @Param("status") String status);

                int cancelTasksById(@Param("taskId") Long taskId);
}
