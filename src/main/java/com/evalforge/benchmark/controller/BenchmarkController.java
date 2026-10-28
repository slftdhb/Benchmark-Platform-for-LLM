package com.evalforge.benchmark.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.evalforge.benchmark.dto.CreateBenchmarkRequest;
import com.evalforge.benchmark.dto.UpdateBenchmarkRequest;
import com.evalforge.benchmark.dto.BenchmarkResponse;
import com.evalforge.benchmark.service.BenchmarkCreateFacade;
import com.evalforge.benchmark.service.BenchmarkService;
import com.evalforge.idempotency.IdempotencyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import com.evalforge.benchmark.dto.PageResponse;

@Tag (
    name = "Benchmark Management",
    description = "Manage benchmark datasets and some operation of benchmark"
)

@RestController
@RequestMapping("/api/v1/benchmarks")
public class BenchmarkController{

        private final BenchmarkService benchmarkService;

        private final IdempotencyService idempotencyService;

        private final BenchmarkCreateFacade benchmarkCreateFacade;

        //构造器依赖注入
        public BenchmarkController(BenchmarkService benchmarkService, IdempotencyService idempotencyService,BenchmarkCreateFacade benchmarkCreateFacade) {
            this.benchmarkService = benchmarkService;
            this.idempotencyService=idempotencyService;
            this.benchmarkCreateFacade=benchmarkCreateFacade;
        }
        @Operation(
            summary = "Create a benchmark",
            description = "Upload a json to create a benchmark"
        )
        //创建benchmark
        @PostMapping
        public Long create(@Valid @RequestBody CreateBenchmarkRequest request, @Size (min = 1 ,max =128) @NotBlank  @RequestHeader("Idempotency-Key") String idempotencyKey){
            return benchmarkCreateFacade.crateBenchmarkWithRedisCache(
                    request,
                    idempotencyKey
            );
        }

        @Operation(
            summary = "qurey by page",
            description = "Get all Benchmarks by page and size"
        )
        //分页查询
        @GetMapping
        public PageResponse<BenchmarkResponse> list(@RequestParam(defaultValue = "1") int page ,@RequestParam(defaultValue = "10") int size){
            return benchmarkService.list(page,size);
        }

        @Operation(
            summary = "Get Benchmark by ID",
            description = "return benchmark details "
        )
        //查详情
        @GetMapping("/{id}")
        public BenchmarkResponse getById(@Parameter(description = "Bechmark ID") @PathVariable("id") Long id){
            return benchmarkService.getById(id);
        }

        @Operation(
            summary = "modify a benchamrk",
            description = "Modify a benchmark by Benchmark ID and upload a JSON"
        )
        //修改信息
        @PutMapping("/{id}")
        public int update(@Parameter(description = "Benchmark ID") @PathVariable("id") Long id, @Valid @RequestBody UpdateBenchmarkRequest request){
            return benchmarkService.update(id, request);
        }

        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void archive(@PathVariable("id") Long id){
            //将benchmark归档而不是物理删除
            benchmarkService.archive(id);
            
        }

        //查询版本列表
        @GetMapping("/{id}/versions")
        public List<Integer> listVersions(@PathVariable("id") Long id){
            return benchmarkService.listVersions(id);
        }

}