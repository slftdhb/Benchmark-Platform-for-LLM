package com.evalforge.benchmark.importer;

import com.evalforge.benchmark.entity.BenchmarkSample;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.JsonNodeException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;

import com.evalforge.benchmark.dto.BenchmarkSampleImportDTO;    

public class BenchmarkJsonlParser {

    //负责把String转成jsonDTO

    private final ObjectMapper objectMapper = new ObjectMapper();


    //读取一行Jsonl的内容并解析成BenchmarkSampleImportDTO对象
    public BenchmarkSampleImportDTO parse(String jsonLine) throws JsonNodeException {
        JsonNode rootNode = objectMapper.readTree(jsonLine);

        String sampleKey = rootNode.get("sample_key") ==null ? null : rootNode.get("sample_key").asText();
        JsonNode inputNode = rootNode.get("input");
        JsonNode goldNode = rootNode.get("gold");
        JsonNode metadataNode = rootNode.get("metadata");

        return new BenchmarkSampleImportDTO(
            sampleKey,
            inputNode,
            goldNode,
            metadataNode
        );
    
    }


}