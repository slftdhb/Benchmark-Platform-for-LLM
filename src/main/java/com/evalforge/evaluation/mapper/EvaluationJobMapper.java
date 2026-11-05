package com.evalforge.evaluation.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.evalforge.evaluation.dto.CreateEvaluationJobRequest;
import com.evalforge.evaluation.dto.EvaluationJobResponse;
import com.evalforge.evaluation.entity.EvaluationJob;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.ognl.Evaluation;

@Mapper 
public interface EvaluationJobMapper {

            int createJob(@Param("job") EvaluationJob job);

            EvaluationJob getJobById(@Param("userId") Long userId, @Param("jobId") Long jobId);

            List<EvaluationJob> getJobsByUserId(@Param("userId") Long userId);

            int cancelJob(@Param("userId") Long userId, @Param("jobId") Long jobId);
}
