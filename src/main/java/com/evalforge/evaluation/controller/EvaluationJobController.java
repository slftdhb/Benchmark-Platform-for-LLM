package com.evalforge.evaluation.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evalforge.evaluation.dto.CreateEvaluationJobRequest;
import com.evalforge.evaluation.dto.EvaluationJobResponse;
import com.evalforge.evaluation.service.EvaluationJobCreateFacade;
import com.evalforge.evaluation.service.EvaluationJobService;
import com.evalforge.idempotency.IdempotencyService;
import com.evalforge.security.CurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Tag (
    name = "evaluationJob manage"
)
@RestController 
@RequestMapping("/api/v1/evaluation-jobs")
public class EvaluationJobController {
            private final EvaluationJobService evaluationJobService;
            private final IdempotencyService idempotencyService;
            private final EvaluationJobCreateFacade jobCreateFacade;

            public EvaluationJobController(EvaluationJobService evaluationJobService, IdempotencyService idempotencyService,EvaluationJobCreateFacade jobCreateFacade) {
                this.evaluationJobService = evaluationJobService;
                this.idempotencyService = idempotencyService;
                this.jobCreateFacade= jobCreateFacade;
            }

            @Operation(
                summary = "create a new evaluation job with idempotency key"
            )
            @PostMapping 
            public Long createEvaluationJob(@AuthenticationPrincipal CurrentUser currentUser,
                                            @Valid @RequestBody CreateEvaluationJobRequest createEvaluationJobRequest,
                                            @Size (min = 1 , max = 128) @NotBlank @RequestHeader("Idempotency-Key") String idempotencyKey

            ){
                return jobCreateFacade.createEvaluationJobWithCache(currentUser.id(), createEvaluationJobRequest, idempotencyKey);
            }

            @Operation (
                summary = "get an evaluation job details by ID on the current user"
            )
            @GetMapping("/{id}")
            public EvaluationJobResponse getEvaluationJob(@AuthenticationPrincipal CurrentUser currentUser,@PathVariable("id") Long jobId){
                return evaluationJobService.getEvaluationJob(currentUser.id(), jobId);
            }

            @Operation(
                summary = "get all evaluation jobs for the current user"
            )
            @GetMapping
            public List<EvaluationJobResponse> getCurrentUserEvaluationJobs(@AuthenticationPrincipal CurrentUser currentUser){
                return evaluationJobService.getCurrentUserEvaluationJobs(currentUser.id());
            }

            @Operation(
                summary = "cancel an evaluation job by ID on the current user"
            )
            @PostMapping("/{id}/cancel")
            public void cancelEvaluationJob(@AuthenticationPrincipal CurrentUser currentUser,@PathVariable("id") Long jobId){
                evaluationJobService.cancelEvaluationJob(currentUser.id(), jobId);
            }

            @Operation(
                summary = "simulate an evaluation job by ID on the current user"
            )
            @PostMapping("/{id}/simulate")
            public void simulateEvaluationJob(@AuthenticationPrincipal CurrentUser currentUser,@PathVariable("id") Long jobId){
                evaluationJobService.simulateEvaluationJob(currentUser.id(), jobId);
            }

}
