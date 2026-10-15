package com.evalforge.benchmark.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.evalforge.benchmark.dto.BenchmarkSampleResponse;
import com.evalforge.benchmark.entity.BenchmarkSample;
import java.util.List;

@Mapper
public interface BenchmarkSampleImportMapper {

    //批次插入
    int insertBatch(@Param ("batch") List<BenchmarkSample> batch);

    //行插入
    int insertLine(@Param ("sample") BenchmarkSample sample);

    //分页查询
    List<BenchmarkSample> selectPage(@Param("versionId") Long versionId, @Param("offset") int offset, @Param("size") int size);

    //统计样本数
    long countByVersionId(@Param("versionId") Long versionId);
}
