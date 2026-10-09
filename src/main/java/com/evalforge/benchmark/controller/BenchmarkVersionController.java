package com.evalforge.benchmark.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import com.evalforge.benchmark.entity.BenchmarkVersion;
import com.evalforge.benchmark.service.BenchmarkVersionService;
import com.evalforge.benchmark.dto.UpdateBenchmarkVersionRequest;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1")
public class BenchmarkVersionController {

        private final BenchmarkVersionService benchmarkVersionService;

        //构造器依赖注入
        public BenchmarkVersionController(BenchmarkVersionService benchmarkVersionService) {
                this.benchmarkVersionService = benchmarkVersionService;
        }


        //创建草稿版本
        @PostMapping("/benchmarks/{id}/versions")
        public Long createDraftVersion(@PathVariable("id") Long id){
             return benchmarkVersionService.createDraftVersion(id);
        }


        //修改草稿信息
        @PutMapping("/benchmark-versions/{id}")
        public int updateDraftNotes(@PathVariable("id") Long id, @Valid @RequestBody UpdateBenchmarkVersionRequest updateBenchmarkVersionRequest){
            return benchmarkVersionService.updateDraftNotes(id, updateBenchmarkVersionRequest);
        }

        //发布版本
        @PostMapping("/benchmark-versions/{id}/publish")
        public void publishVersion(@PathVariable("id") Long id){
            benchmarkVersionService.publishVersion(id);
        }


}