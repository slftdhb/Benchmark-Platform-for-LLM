package com.evalforge.benchmark.mapper;

import com.evalforge.benchmark.entity.BenchmarkVersion;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.evalforge.benchmark.dto.UpdateBenchmarkVersionRequest;

@Mapper
public interface BenchmarkVersionMapper{
    int insert (BenchmarkVersion benchmarkVersion);

    BenchmarkVersion selectById(Long id);

    List<BenchmarkVersion> selectByBenchmarkId(
            Long benchmarkId
    );

    //根据benchmarkID检查是否已有DRAFT存在
    int countDraftByBenchmarkId(@Param("benchmarkId") Long benchmarkId);

    //查询最大版本号
    Integer selectMaxVersionNoByBenchmarkId(@Param("benchmarkId") Long benchmarkId);

    //传递给mybatis参数信息
    int updateDraftNotes(@Param("id") Long id, @Param("request") UpdateBenchmarkVersionRequest UpdateBenchmarkVersionRequest);

    int publishVersion(Long id);
}