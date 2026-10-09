package com.evalforge.benchmark.mapper;

import com.evalforge.benchmark.entity.Benchmark;
import java.util.Optional;
import com.evalforge.benchmark.dto.UpdateBenchmarkRequest;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import com.evalforge.benchmark.dto.BenchmarkResponse;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BenchmarkMapper{

    int insert(Benchmark benchmark);

    Optional<BenchmarkResponse> selectById(Long id);

    //这里使用@Param明确说明传递给mybatis的参数名称，再在xml中明确的通过#{id}和#{request.xxx}来引用这些参数,否则mybatis封装参数，导致无法识别
    int update(@Param("id") Long id , @Param("request") UpdateBenchmarkRequest request);
    
    int archive(Long id);

    Long count();

    List<Benchmark> selectAll();

    List<BenchmarkResponse> list();

    List<BenchmarkResponse> selectPage(long offset, int size);

}