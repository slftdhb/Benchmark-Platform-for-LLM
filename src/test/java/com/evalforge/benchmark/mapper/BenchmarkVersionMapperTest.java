package com.evalforge.benchmark.mapper;

import com.evalforge.benchmark.entity.Benchmark;
import com.evalforge.benchmark.entity.BenchmarkVersion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.List;
import com.evalforge.benchmark.dto.UpdateBenchmarkVersionRequest;

@SpringBootTest
@Transactional
class BenchmarkVersionMapperTest {
    
    @Autowired
    private BenchmarkVersionMapper benchmarkVersionMapper;

    @Autowired
    private BenchmarkMapper benchmarkMapper;



}