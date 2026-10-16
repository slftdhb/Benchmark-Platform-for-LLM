package com.evalforge.benchmark.importer;

import com.evalforge.benchmark.dto.BenchmarkSampleImportDTO;
import com.evalforge.benchmark.exception.BenchmarkImportException;
import com.evalforge.common.error.ErrorCode;

import tools.jackson.databind.node.ObjectNode;


public class BenchmarkSampleValidator {

    //校验数据字段是不是合法
    public void validate(BenchmarkSampleImportDTO sample,int lineNo){
        
        // 校验 sampleKey 是否为空
            if(sample.sampleKey()==null || sample.sampleKey().isBlank()){
                    throw new BenchmarkImportException(lineNo,"sampleKey is required");
        }       
        //校验 sampleKey 长度是否超过 128
            if(sample.sampleKey().length() > 128){
                    throw new BenchmarkImportException(lineNo,"sampleKey cannot exceed 128 characters");
        }

        // 校验 input 是否为对象
            if(sample.input() ==null || !sample.input().isObject()){
                    throw new BenchmarkImportException(ErrorCode.VALIDATION_FAILED,lineNo,"input is required and must be an object");
        }
        //检验是不是json
            if(!(sample.input() instanceof ObjectNode)){
                    throw new BenchmarkImportException(ErrorCode.VALIDATION_FAILED,lineNo, "input must be a JSON");
            }
        //校验 gold 是否存在，格式是不是正确
            if(sample.gold() != null){
                if(! (sample.gold() instanceof ObjectNode)){
                        throw new BenchmarkImportException(lineNo,"gold is required and must be an JSON object");
                }
            }
        //如果metadata存在，是不是对象
            if(sample.metadata() !=null){
                if(! (sample.metadata() instanceof ObjectNode) ){
                        throw new BenchmarkImportException(lineNo,"metadata is not  an JSON object");
                }
            }
            
}

}