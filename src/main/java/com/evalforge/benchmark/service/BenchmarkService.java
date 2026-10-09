package com.evalforge.benchmark.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.evalforge.benchmark.entity.Benchmark;
import com.evalforge.benchmark.entity.BenchmarkVersion;
import org.springframework.transaction.annotation.Transactional;

import com.evalforge.benchmark.mapper.BenchmarkMapper;
import com.evalforge.benchmark.mapper.BenchmarkVersionMapper;
import com.evalforge.benchmark.dto.CreateBenchmarkRequest;
import com.evalforge.benchmark.dto.UpdateBenchmarkRequest;
import com.evalforge.benchmark.dto.BenchmarkResponse;
import com.evalforge.benchmark.exception.BenchmarkNotFoundException;
import org.apache.ibatis.annotations.Param;
import com.evalforge.benchmark.dto.PageResponse;


@Service
public class BenchmarkService {

    private final BenchmarkMapper benchmarkMapper;
    private final BenchmarkVersionMapper versionMapper;

    //构造器注入
    public BenchmarkService(BenchmarkMapper benchmarkMapper, BenchmarkVersionMapper versionMapper){
        this.benchmarkMapper = benchmarkMapper;
        this.versionMapper = versionMapper;
    }

    @Transactional
    public Long create(CreateBenchmarkRequest request){
        Benchmark benchmark = new Benchmark();
        benchmark.setName(request.name());
        benchmark.setStatus("ACTIVE");
        benchmark.setDescription(request.description());

        benchmarkMapper.insert(benchmark);

        //利用mybatis回填的id进行版本初始化和插入
        BenchmarkVersion version =new BenchmarkVersion();
        version.setBenchmarkId(benchmark.getId());
        version.setVersionNo(1);
        version.setStatus("DRAFT");
        versionMapper.insert(version);

        return benchmark.getId();
    }   


    //分页查询
    public PageResponse<BenchmarkResponse> list(int page, int size){   
        //参数校验
        if(page < 1){
            throw new IllegalArgumentException("Page number must be greater than 0");
        }
        if(size < 1 || size > 100){
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }
        //计算偏移量
        long offset = (page -1 ) * size; 
        List<BenchmarkResponse> data = benchmarkMapper.selectPage(offset,size);
        long total = benchmarkMapper.count();
        return new PageResponse<>(data,total,page,size);

    }

    //查详情
    public BenchmarkResponse getById(long id){
        return benchmarkMapper.selectById(id).orElseThrow(()-> {
            return new BenchmarkNotFoundException(id);
        });
    }

    //修改信息
    //不能存在空名（@Valid就能干），不存在重复名称，不允许修改不存在的benchmark，修改benchmark的描述，不应该改变已发布版本的信息
    @Transactional
    public int update(Long id, UpdateBenchmarkRequest request){
            BenchmarkResponse benchmarkResponse = benchmarkMapper.selectById(id).orElseThrow(()-> {
                return new BenchmarkNotFoundException(id);
            });
    

            List<BenchmarkResponse> allbenchmarks = benchmarkMapper.list();

            for(BenchmarkResponse benchmark : allbenchmarks){
                if(benchmark.name().equals(request.name()) && !benchmark.id().equals(id)){
                    throw new RuntimeException("Benchmark name already exists");
                }
            }

            return benchmarkMapper.update(id,request);
            
    }

    
    //删除benchmark
    @Transactional
    public void archive(Long id){
        int affectedRows =benchmarkMapper.archive(id);
        if(affectedRows == 0){
            throw new BenchmarkNotFoundException(id);
        }
    }

    //查询版本列表
    public List<Integer> listVersions(Long id){
        List<BenchmarkVersion> versionList = versionMapper.selectByBenchmarkId(id);
        return versionList.stream().map(BenchmarkVersion::getVersionNo).toList();
    }


}