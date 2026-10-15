package com.evalforge.benchmark.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.evalforge.benchmark.dto.BenchmarkImportResponse;
import com.evalforge.benchmark.dto.BenchmarkSampleResponse;
import com.evalforge.benchmark.dto.PageResponse;
import com.evalforge.benchmark.service.BenchmarkImportService;

@RestController 
@RequestMapping("api/v1")
public class BenchmarkImportController {
    
    private final BenchmarkImportService benchmarkImportService;
    
    public BenchmarkImportController(BenchmarkImportService benchmarkImportService) {
        this.benchmarkImportService = benchmarkImportService;
    }

    //上传json文件的接口
    @PostMapping(value  = "benchmark-versions/{versionId}/samples/import",
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE) 
    public BenchmarkImportResponse importSamples(
        @PathVariable Long versionId,
        @RequestPart("file") MultipartFile file
    ){
        return benchmarkImportService.importJsonl(versionId, file);
    }

    //分页查询
    @GetMapping("benchmark-versions/{versionId}/samples")
    public PageResponse<BenchmarkSampleResponse> listSamples(@PathVariable( "versionId") Long versionId,@RequestParam(defaultValue = "1") int page , @RequestParam(defaultValue = "10") int size){
        return benchmarkImportService.listSamples(versionId,page,size);
    }


}