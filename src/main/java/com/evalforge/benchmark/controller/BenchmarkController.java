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
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.evalforge.benchmark.dto.CreateBenchmarkRequest;
import com.evalforge.benchmark.dto.UpdateBenchmarkRequest;
import com.evalforge.benchmark.dto.BenchmarkResponse;
import com.evalforge.benchmark.service.BenchmarkService;
import java.util.List;
import com.evalforge.benchmark.dto.PageResponse;


@RestController
@RequestMapping("/api/v1/benchmarks")
public class BenchmarkController{

        private final BenchmarkService benchmarkService;

        //构造器依赖注入
        public BenchmarkController(BenchmarkService benchmarkService) {
            this.benchmarkService = benchmarkService;
        }

        //创建benchmark
        @PostMapping
        public Long create(@Valid @RequestBody CreateBenchmarkRequest request){
            return benchmarkService.create(request);
        }


        //分页查询
        @GetMapping
        public PageResponse<BenchmarkResponse> list(@RequestParam(defaultValue = "1") int page ,@RequestParam(defaultValue = "10") int size){
            return benchmarkService.list(page,size);
        }

        //查详情
        @GetMapping("/{id}")
        public BenchmarkResponse getById(@PathVariable("id") Long id){
            return benchmarkService.getById(id);
        }

        //修改信息
        @PutMapping("/{id}")
        public int update(@PathVariable("id") Long id, @Valid @RequestBody UpdateBenchmarkRequest request){
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