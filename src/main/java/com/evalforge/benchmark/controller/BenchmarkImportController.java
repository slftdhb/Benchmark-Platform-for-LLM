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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
    name = "benchmark sample jsonl import ",
    description = "some operations for import jsonl for benchmark version"
)
@RestController 
@RequestMapping("api/v1")
public class BenchmarkImportController {
    
    private final BenchmarkImportService benchmarkImportService;
    
    public BenchmarkImportController(BenchmarkImportService benchmarkImportService) {
        this.benchmarkImportService = benchmarkImportService;
    }

    @Operation (
            summary = "Import jsonl",
            description = "Import jsonl for Benchmark that not archived and has DRAFT version"+"\n"+
                          "Every line must be jsonl"+"\n"+
                          "sample_key cannot be null and  cannot duplicate"+"\n"+
                          "input must be json and gold could be null but if not null,it shoule be json too"+"\n"+
                          "transantion will roll back when import filed"+"\n"+
                          "function return benchmarkImportResponse when success and an ApiError Body when failed"
    )
    //上传json文件的接口
    @PostMapping(value  = "benchmark-versions/{versionId}/samples/import",
                consumes =  MediaType.MULTIPART_FORM_DATA_VALUE) 
    public BenchmarkImportResponse importSamples(
        @Parameter(description = "A Benchmark version ID") @PathVariable Long versionId,
        @Parameter(description = "JSONL file") @RequestPart("file") MultipartFile file
    ){
        return benchmarkImportService.importJsonl(versionId, file);
    }

    @Operation (
        summary = "get samples for versionId",
        description = "get all samples for VersionID by page(default=1) and size(default=10)"
    )
    //分页查询
    @GetMapping("benchmark-versions/{versionId}/samples")
    public PageResponse<BenchmarkSampleResponse> listSamples(@PathVariable( "versionId") Long versionId,@RequestParam(defaultValue = "1") int page , @RequestParam(defaultValue = "10") int size){
        return benchmarkImportService.listSamples(versionId,page,size);
    }

}